package socket.Decode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;


import com.canplay.milk.mvp.LogModel.LogsModel;
import com.canplay.milk.util.ConfigUtils;

import java.util.ArrayList;


import de.greenrobot.event.EventBus;
import socket.LaserConstant;
import socket.bean.MilkConstant;
import socket.core.ConnectManager;
import socket.laserprotocol.AllargProtocol;
import socket.laserprotocol.BaseLaserDate;
import socket.laserprotocol.Baseprotocol;
import socket.laserprotocol.ControlProtocol;
import socket.laserprotocol.LoginProtocol;
import socket.laserprotocol.OperateProtocol;
import socket.laserprotocol.RailwayProtocol;
import socket.laserprotocol.SubscribeProtocol;
import socket.laserprotocol.device.DeviceProtocol;
import socket.laserprotocol.entry.LaserLog;
import socket.laserprotocol.entry.LogProtocol;
import socket.util.HandlerUtils;
import socket.util.HexUtil;

/**
 * Created by linquandong on 16/12/10.
 */
public class SvrCommnuManager {

    public  int environment = ConfigUtils.getEnv(); //0：表示是局域网，1：表示是互联网
    private static SvrCommnuManager instance;
    private LogProtocol lastLog;

    private String deviceName = "";
    private SvrCommnuManager() {

    }

    public String getDeviceName(){
        if(!ConnectManager.getInstance().getEventConnect().isConnected()){
           return "";
        }
        return deviceName;
    }

    /**
     * 更新链接的设备名，并且更新改设备的最新记录
     * @param deviceName
     */
    public void setDeviceName(String deviceName){
        if(!this.deviceName.equals(deviceName)){
            this.deviceName = deviceName;
            lastLog = LogsModel.getLaseRecord();
            if(lastLog == null){
                lastLog = new LogProtocol();
            }
        }
    }
    public void setRegister(boolean register,Object observer) {
        if (register) {
            if (!EventBus.getDefault().isRegistered(observer)) {
                EventBus.getDefault().register(observer);
            }
        } else {
            if (EventBus.getDefault().isRegistered(observer)) {
                EventBus.getDefault().unregister(observer);
            }
        }
    }

    public static SvrCommnuManager shareInstance() {
        if (instance == null) {
            instance = new SvrCommnuManager();
        }
        return instance;
    }

    public void sendProtolRequest(Baseprotocol protocol) {
        String commnuStr ="";
        if(environment == ConfigUtils.LAN){
            commnuStr =  LanUtils.shareIntance().getTranData(protocol);
        }else{
            commnuStr = NetUtils.shareIntance().getTranData(protocol);
        }
        ConnectManager.getInstance().sendMessage(commnuStr);
    }

//    encodeHexStr
    /**
     * 信息一回来就会调用该方法来解析
     * @param msg
     * @return
     */
    public BaseLaserDate encode(String msg) {
        if(environment == ConfigUtils.LAN){
           return LanUtils.shareIntance().encodeDate(msg);
        }else {
            return NetUtils.shareIntance().encodeDate(msg);
        }
    }

    public void close() {

        if(ControlProtocol.controlAble()){
            ConnectManager.getInstance().cancelAll();
            sendProtolRequest(new ControlProtocol(false));
            HandlerUtils.postOnMainWithDelay(new Runnable() {
                @Override
                public void run() {
                    ConnectManager.getInstance().disConnect();
                }
            },1000);
            return;
        }
        ConnectManager.getInstance().disConnect();

    }

    /**
     * 分发数据
     * @param data
     * @param ret
     */
    public void disPatchData(BaseLaserDate data,int ret) {
        switch (ret){
            case 0:
                if(environment == ConfigUtils.LAN){
                    responeProtocl(data);
                }else{
                    responeNetProtocl(data);
                }
                break;
        }
//        Message message = new Message();
//        message.obj = data;
//        message.what = ret;
//        mainHandler.sendMessage(message);
    }

    public LaserLog getLastLog(){
        try {
            return lastLog.makeLog().get(0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<LaserLog> getLastLogProtocolErrorList(){
        if(lastLog != null){
            LogProtocol Log = lastLog.clone();
            return Log.getAllErrorLog();
        }
        return new ArrayList<>();

    }

    /***
     * 解析互联网数据，进行分发
     * @param data
     */
    private void responeNetProtocl(BaseLaserDate data) {
        //全通道数据
        if (data.identify(LaserConstant.ALLARG_S)){
            try {
                AllargProtocol protocol = CacheUtils.shareInstance().getProtocol(LaserConstant.ALLARG_S);
                protocol.encode(data);

                //设备名称赋值,获取该设备的最新的记录
                setDeviceName(protocol.getName());

                //保存操作日志
                //满足变化条件则保存到本地数据库
                if(lastLog.edcode(protocol.getName(),protocol.controlState,protocol.faultState)){
                    LogProtocol Log = lastLog.clone();
                    if(Log != null && Log.isWirable()){
                        Log.save();
                    }
                }

                send2MainThread(protocol);
            }catch (Exception e){
                e.printStackTrace();
            }

            return;
        }


        if(data.identify(LaserConstant.DEVICE)){//设备列表
            DeviceProtocol protocol = CacheUtils.shareInstance().getProtocol(LaserConstant.DEVICE);
            protocol.encode(data);
            send2MainThread(protocol);
            return;
        }
        if(data.identify(LaserConstant.SUBSCRIBE)){//订阅
            SubscribeProtocol protocol = CacheUtils.shareInstance().getProtocol(LaserConstant.SUBSCRIBE);
            protocol.encode(data);
            send2MainThread(protocol);
            return;
        }
        if(data.identify(LaserConstant.RAILWAY)){//申请专线
            RailwayProtocol protocol = CacheUtils.shareInstance().getProtocol(LaserConstant.RAILWAY);
            protocol.encode(data);
            send2MainThread(protocol);
            return;
        }
        if(data.identify(LaserConstant.OPERATE)){
            OperateProtocol protocol = CacheUtils.shareInstance().getProtocol(LaserConstant.OPERATE);
            protocol.encodeBusId(data);
            send2MainThread(protocol);
            return;
        }

        if(data.identify(LaserConstant.NETCONTROL)){
            ControlProtocol protocol = CacheUtils.shareInstance().getProtocol(LaserConstant.CONTROL);
            protocol.encodeNet(data);
            send2MainThread(protocol);
            return;
        }
    }
    /***
     * 解析局域网数据，进行分发
     * @param data
     */
    private void responeProtocl(BaseLaserDate data) {
        if (data.identify(LaserConstant.ALLARG_S)){
            AllargProtocol protocol = CacheUtils.shareInstance().getProtocol(LaserConstant.ALLARG_S);
            protocol.encode(data);
            //设备名称赋值,获取该设备的最新的记录
            setDeviceName(protocol.getName());
            //保存操作日志
            //满足变化条件则保存到本地数据库
            if(lastLog.edcode(protocol.getName(),protocol.controlState,protocol.faultState)){
                LogProtocol Log = lastLog.clone();
                if(Log != null && Log.isWirable()){
                    Log.save();
                }
            }
            send2MainThread(protocol);
//            EventBus.getDefault().post(protocol);
            return;
        }
        if(data.identify(LaserConstant.LOGIN)){
            LoginProtocol protocol = CacheUtils.shareInstance().getProtocol(LaserConstant.LOGIN);
            protocol.encode(data);
            send2MainThread(protocol);
            return;
        }
        if(data.identify(LaserConstant.OPERATE)){
            OperateProtocol protocol = CacheUtils.shareInstance().getProtocol(LaserConstant.OPERATE);
            protocol.encodeBusId(data);
            //局域网的控制帧在operate里面
            if(LaserConstant.NETCONTROL.equals(protocol.getBusId())){
                ControlProtocol controlProtocol = CacheUtils.shareInstance().getProtocol(LaserConstant.CONTROL);
                controlProtocol.encodeOperate(protocol);
                send2MainThread(controlProtocol);
            }else{
                send2MainThread(protocol);
            }
            return;
        }


    }

    public <T extends Baseprotocol> void send2MainThread  (final T data){
        HandlerUtils.postOnMain(new Runnable() {
            @Override
            public void run() {
                if(EventBus.getDefault().hasSubscriberForEvent(data.getClass())){
                    EventBus.getDefault().post(data);
                }
            }
        });
    }


    public Handler mainHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    BaseLaserDate date = (BaseLaserDate) msg.obj;
                    responeProtocl(date);
                    break;
            }
        }
    };

    public boolean shouldSendmsg(BaseLaserDate data) {
        if(environment == ConfigUtils.LAN){
            return LanUtils.shareIntance().shouldSendmsg(data);
        }else{
            return NetUtils.shareIntance().shouldSendmsg(data);
        }
    }

    public void disPatchFailData(BaseLaserDate data) {

    }

}
