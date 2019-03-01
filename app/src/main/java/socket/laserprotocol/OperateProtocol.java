package socket.laserprotocol;


import com.canplay.milk.util.ConfigUtils;

import socket.LaserConstant;
import socket.laserprotocol.operate.Operate;

/**
 * Created by linquandong on 16/12/5.
 * 操作协议
 */
public class OperateProtocol extends Baseprotocol {
    public Operate mOperate;
    private String busId = "";
    private String operateLen;
    private String operateStr;

    public OperateProtocol(Operate Operate){
        controlStr = LaserConstant.OPERATE;
        mOperate = Operate;
    }
    public OperateProtocol(){}

    @Override
    public String makeCommunicateStr() {
        String hefix = controlStr;
        String data = mOperate.makeCommunicateStr();
        return makCommnuByHeaderData(hefix,data);
    }

    @Override
    public String makeSvrCmdStr() {
        return ConfigUtils.getRailwayFlag()+mOperate.makeCommunicateStr();
    }

    public String encodeBusId(BaseLaserDate laserDate) {
        super.encode(laserDate);
         busId = busStr.substring(0,2);
         operateLen = busStr.substring(2,4);
         operateStr = busStr.substring(4,busStr.length());

        //检查长度
         int len = Integer.parseInt(operateLen,16);
         int actlen = operateStr.length()/2;
        return  len == actlen? busId:"";
    }

    public String getBusId() {
        return busId;
    }

    public String getOperateStr() {
        return operateStr;
    }

//    public Operate encodeOperate(BaseLaserDate laserDate) {
//        super.encode(laserDate);
//        String busId = busStr.substring(0,2);
//        String operateLen = busStr.substring(2,4);
//        String operate = busStr.substring(4,busStr.length()-1);
//        return  makeOperate(busId,operate);
//    }
//
//    private Operate makeOperate(String busId, String operate) {
//        if (busId.equals(LaserConstant.REDLIGHT)){
//            mOperate = new RedLight(operate);
//            return mOperate;
//        }
//        if(busId.equals(LaserConstant.LASERLIGHT)){
//            mOperate = new LaserLight(operate);
//            return mOperate;
//        }
//        return mOperate;
//    }
}
