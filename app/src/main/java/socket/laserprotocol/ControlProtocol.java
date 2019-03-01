package socket.laserprotocol;

;

import com.canplay.medical.R;
import com.canplay.milk.util.ConfigUtils;
import com.canplay.milk.util.FR;

import java.util.Calendar;
import java.util.Random;

import socket.Decode.SvrCommnuManager;
import socket.LaserConstant;
import socket.core.ConnectManager;
import socket.laserprotocol.entry.ControlState;
import socket.util.HexUtil;

/**
 * Created by linquandong on 16/12/5.
 * 授权管理
 */
public class ControlProtocol extends Baseprotocol {

    private static int random;
    private static int randomVerify;
    private static boolean isApply;
    private static boolean isSuccess;
    private static boolean isSupper;
    private static boolean isCancel;
    public String data;
    public String status;



    public ControlProtocol(boolean isRequest){
        if(isRequest){
            String header = "01"; //申请控制权
            random = getRandom(4);   //4位的随机数
            String randomStr = String.format("%04x",random);
            data = header+randomStr;
        }else{
            data = "00"; //撤销控制权
        }
        controlStr = HexUtil.or(LaserConstant.NETCONTROL, "80");
    }

    /**
     * 必须要默认构造函数，用于反射
     */
    public ControlProtocol(){

    }
    public static boolean isCancel() {
        return isCancel;
    }

    public static void setControlState(String controlState) {
       boolean isCurrentSuccess = ControlState.shareInstance().sate(controlState, ControlState.CONTROL_RIGHT);
        //如果是互联网，而且被pc端断开控制权，需要断开专线
        if(isSuccess && !isCurrentSuccess && ConfigUtils.getEnv() == ConfigUtils.WAN){
            SvrCommnuManager.shareInstance().sendProtolRequest(new RailwayProtocol(null));
        }
       isSuccess = isCurrentSuccess;
    }

    private int getRandom(int num) {
        StringBuilder str=new StringBuilder();//定义变长字符串
        Random random=new Random();
        for(int i=0;i<num;i++){
            int value = random.nextInt(10);
            if(i == 0 && value ==0){ //杜绝第一位为0导致少一位数
                value = 1;
            }
            str.append(value);
        }
        return Integer.parseInt(str.toString());
    }


    @Override
    public String makeCommunicateStr() {
        String hefix = LaserConstant.OPERATE;
        String contrl = makCommnuByHeaderData(controlStr,data);
        return makCommnuByHeaderData(hefix,contrl);
    }

    @Override
    public String makeSvrCmdStr() {
        return ConfigUtils.getRailwayFlag()+makCommnuByHeaderData(controlStr,data);
    }

    public void encodeOperate(OperateProtocol operateProtocol) {
        busStr = operateProtocol.getOperateStr();
        encodeBusStr(busStr);
    }

    private void encodeBusStr(String busStr) {
        isApply = false;
        isCancel = false;
        if(busStr.equals("0100")){
            status = FR.str(R.string.apply_Fail);
        }
        if(busStr.equals("0101")){
            status = FR.str(R.string.apply_success);
            isApply = true;
        }
        if(busStr.equals("0001")){//撤销授权申请
            isCancel = true;
        }
    }

    public void encodeNet(BaseLaserDate data) {
        busStr = data.busStr;
       encodeBusStr(busStr);
    }

    public static boolean controlAble(){
        if(ConnectManager.getInstance().getRailwayhasBroken()){
            isSupper = false;
        }

        if(isSupper){
            return true;
        }

        return isSuccess && isApply && !ConnectManager.getInstance().getRailwayhasBroken() && randomVerify==random;
    }

    public static boolean isApplyed(){
        return isApply;
    }
    public static  boolean isIsSupper(){
        return isSupper;
    }

    public static String getRandom(){
        return random+"";
    }

    public static boolean setRandomVerify(int value){
        Calendar calendar = Calendar.getInstance();
        int cHour = calendar.get(Calendar.HOUR_OF_DAY);
        int cMinte = calendar.get(Calendar.MINUTE);
        int hour = value/100;
        int minte = value%100;
        if(hour == cHour && minte == cMinte){
            isSupper = true;
            //如果后门成功撤销控制命令
            SvrCommnuManager.shareInstance().sendProtolRequest(new ControlProtocol(false));

            return isSupper;
        }
        randomVerify = value;
        return randomVerify == random;
    }
}
