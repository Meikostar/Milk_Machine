package udpsocket.core;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.blanke.xsocket.tcp.client.TcpConnConfig;
import com.blanke.xsocket.tcp.client.XTcpClient;
import com.blanke.xsocket.tcp.client.bean.TargetInfo;
import com.blanke.xsocket.tcp.client.bean.TcpMsg;
import com.blanke.xsocket.tcp.client.helper.stickpackage.AbsStickPackageHelper;
import com.blanke.xsocket.tcp.client.listener.TcpClientListener;
import com.blanke.xsocket.utils.StringValidationUtils;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.mvp.activity.home.PushMilkActivity;
import com.canplay.milk.mvp.activity.mine.WifiSettingActivity;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.mykar.framework.activeandroid.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import socket.bean.MilkConstant;
import socket.util.HexUtil;
import socket.util.WifiUtils;

/**
 * Created by Meikostar on 2018/6/7.
 */

public class TcpClientManager implements  TcpClientListener {

    /**
     * 单例对象模式，不同的Activity共享同一个ScoketClientMgr
     */
    private static TcpClientManager instance = null;
    private Socket clientSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;

    private int port = 5000;
    public boolean isStop = false;
    private SocketClientCallBack callBack;

    public synchronized static TcpClientManager getInstance() {
        if (instance == null) {
            instance = new TcpClientManager();
            handler= new Handler();
        }
        return instance;
    }

    /**
     * 通过这方法回调数据
     *
     * @param callBack
     */
    public void getTcpMessage(SocketClientCallBack callBack) {
        this.callBack = callBack;
    }


    public XTcpClient xTcpClient;

    public boolean isConnected() {
        return xTcpClient!=null? xTcpClient.isConnected():false;
    }
    /**
     * 创建客户端
     *
     */
    public void startTcpClient() {

        if (xTcpClient != null && xTcpClient.isConnected()) {
            xTcpClient.disconnect();
            xTcpClient=null;
            StaticPackage.selectPosition=0;
        } else {

            AbsStickPackageHelper stickHelper = StaticPackage.getStickPackageHelper();
            if ( StringValidationUtils.validateRegex(BaseApplication.ip, StringValidationUtils.RegexIP)
                    && StringValidationUtils.validateRegex(port+"", StringValidationUtils.RegexPort)) {
                TargetInfo targetInfo = new TargetInfo(BaseApplication.ip, port);
                xTcpClient = XTcpClient.getTcpClient(targetInfo);
                xTcpClient.addTcpClientListener(this);
                xTcpClient.config(new TcpConnConfig.Builder()
                        .setStickPackageHelper(stickHelper)//粘包
                        .setIsReconnect(false)
                        .create());
                if (xTcpClient.isDisconnected()) {
                    xTcpClient.connect();
                } else {
//                    s("已经存在该连接");
                }
            } else {
//                addMsg("服务器地址必须是 ip:port 形式");
            }
        }
    }

//    private Runnable runSocket = new Runnable() {
//        @Override
//        public void run() {
//            if (clientSocket == null) {
//                try {
//                    clientSocket = new Socket(serverIP, port);
//                    outStream = clientSocket.getOutputStream();
//                    inStream = clientSocket.getInputStream();
//
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    };
    /**
     * 心跳测试线程
     */
    private Runnable runHeartbeat = new Runnable() {
        @Override
        public void run() {
            String phone = SpUtil.getInstance().getString(SpUtil.PHONE);

            String content = "";
            if(TextUtil.isNotEmpty(phone)){
                for(int i=0;i<phone.length();i++){
                    content=content+ "0"+Integer.toHexString(Integer.valueOf(phone.substring(i,i+1)));
                }
                MilkConstant.selectCommnt(8,content);
                Looper.prepare();
                TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(),null);
                Looper.loop();// 进入loop中的循环，查看消息队列

            }

            while (!BaseApplication.stop&&!isStop) {
                if(BaseApplication.state==0){
                    isStop=true;
                }

                MilkConstant.selectCommnt(1,"01");
                SendMessage(MilkConstant.sendCommends(),null);
                try {
                    Thread.sleep(8000);// 正常
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    /**
     * 客户端正常退出调用这个方法
     */
    public void clientQuit() {
        if (xTcpClient != null) {
            xTcpClient.removeTcpClientListener(this);
            xTcpClient.disconnect();//activity销毁时断开tcp连接
            xTcpClient=null;
        }
    }

    /**
     * 发送数据
     *
     * @param strMsg
     * @return
     */
    private static String content;

    public boolean SendMessage(final String strMsg, Context context) {

        if(TextUtil.isNotEmpty(strMsg)){
            if (xTcpClient != null) {
                if(!xTcpClient.isConnected()){
                    return false ;

                }else {
                    byte[] bytes = HexUtil.hexStringToByte(strMsg);
                    if (xTcpClient != null) {
                        xTcpClient.sendMsg(bytes);
                    } else {
//            addMsg("还没有连接到服务器");
                    }
                }
            }


        }else {
          if(context!=null){
//              Looper.prepare();
//              Toast.makeText(context, "你还未链接设备", Toast.LENGTH_SHORT).show();
//              Looper.loop();
//
              if(BaseApplication.send!=6&&BaseApplication.send!=2){
                  BaseApplication.state=0;
                  context.startActivity(new Intent(context, WifiSettingActivity.class));
              }

          }
        }

        return true;
    }


    private void CloseSocket() {
        isStop = true;
        try {
            if (clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(XTcpClient client) {

        startAD();
//        couts=0;
//        if(countTimer==null){
//            countTimer=new CountTimer(8000,8000);
//            countTimer.start();
//        }else {
//            countTimer.cancel();
//            countTimer.start();
//        }
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.OFF,"1"));
        BaseApplication.state=1;
        MilkConstant.HEAD=MilkConstant.HD;
            startHear();


    }
    private Thread thread;
    public void startHear(){
        if(thread==null){
            isStop=false;
            thread= new Thread(runHeartbeat);
            thread.start();
        }else {
            if(isStop){
                isStop=false;
                thread= new Thread(runHeartbeat);
                thread.start();
            }

        }

    }
    @Override
    public void onSended(XTcpClient client, TcpMsg tcpMsg) {
     Log.e("send:"+tcpMsg.getSourceDataString());
    }
    public void disConnect(){
        clientQuit();
        instance=null;

        CloseSocket();
    }
   private int cout=0;
    @Override
    public void onDisconnected(XTcpClient client, String msg, Exception e) {
        Log.e(client.getTargetInfo().getIp() + "断开连接 " + msg + e);
        MilkConstant.HEAD="";
        BaseApplication.state=0;
        StaticPackage.selectPosition=0;

        if(xTcpClient!=null){
            clientQuit();
            instance=null;
            CloseSocket();
        }else {
            instance=null;
            clientSocket=null;
        }
        if(BaseApplication.stop){
            return;
        }
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.RESTART,""));
        BaseApplication.send=6;
    }
    public Runnable runnable = new Runnable() {
        public void run() {


            if(!BaseApplication.stop&&WifiUtils.shareInstance().isUseable()) {

                if (couts == 4) {

                    return;
                } else {
                    couts++;
                    MilkConstant.HEAD="";
                    TcpClientManager.getInstance().disConnect();
                    starBrodcast();
                    startAD();
                }
            }
        }

    };
    public static Handler handler ;
    private int time=9000;
     public void startAD() {

        handler.removeCallbacks(runnable);

        handler.postDelayed(runnable, time);

    }
    private static int couts;
//    private CountTimer countTimer;
//    public class CountTimer extends CountDownTimer {
//
//
//        /**
//         * 参数 millisInFuture       倒计时总时间（如60S，120s等）
//         * 参数 countDownInterval    渐变时间（每次倒计1s）
//         */
//        public CountTimer(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//
//        }
//        // 计时完毕时触发
//        @Override
//        public void onFinish() {
//            MilkConstant.HEAD="";
//            if(WifiUtils.shareInstance().isUseable()){
//
//                if(couts==4){
//                    return;
//                }else {
//
//                    countTimer.start();
//                    couts++;
//                    TcpClientManager.getInstance().disConnect();
//                    starBrodcast();
//                }
//
//            }
//        }
//        // 计时过程显示
//        @Override
//        public void onTick(long millisUntilFinished) {
//
//        }
//    }
    public void starBrodcast(){
        UDPSocketBroadCast broadCast=new UDPSocketBroadCast();
        broadCast.startUDP(new UDPSocketBroadCast.UDPDataCallBack() {
            @Override
            public void mCallback(String str) {
                BaseApplication.ip= getHex(str.substring(22,24))+"."+getHex(str.substring(24,26))+"."+getHex(str.substring(26,28))+"."+getHex(str.substring(28,30));

                if(TextUtil.isNotEmpty(BaseApplication.ip)){
                    MilkConstant.HEAD=str.substring(0,20);
                    MilkConstant.HD=str.substring(0,20);
                    MilkConstant.EQUIPT=str.substring(8,20);
                    TcpClientManager.getInstance().startTcpClient();
                }
            }
        });
    }
    public static String getHex(String cont){
        char[] chars = cont.toCharArray();
        int i=0;
        int ml=0;
        for(char ca:chars){
            if(i==0){
                ml= HexUtil.toDigit(ca,1)*16;
            }else {
                ml=ml+HexUtil.toDigit(ca,1);
            }
            i++;
        }
        return ml+"";
    }
    @Override
    public void onReceive(XTcpClient client, TcpMsg tcpMsg) {
        handler.removeCallbacks(runnable);
        startAD();

        byte[] res = tcpMsg.getSourceDataBytes();
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.NEWINSTANCES,(res!=null&&res.length>0)?HexUtil.bytes2HexString(res):""));


    }

    @Override
    public void onValidationFail(XTcpClient client, TcpMsg tcpMsg) {

    }

    public interface SocketClientCallBack {
        public void callBack(String what);
    }
}
