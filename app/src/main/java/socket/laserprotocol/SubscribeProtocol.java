package socket.laserprotocol;

import socket.LaserConstant;
import socket.laserprotocol.device.LaserDevice;
import socket.util.HexUtil;

/**
 * Created by linquandong on 2017/5/24.
 */

public class SubscribeProtocol extends Baseprotocol {
    LaserDevice mDevice;
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    public int mState;
    private boolean isunRegister; //是否撤销

    public SubscribeProtocol(LaserDevice deivce){
        controlStr = LaserConstant.SUBSCRIBE;
        mDevice = deivce;

    }
    public SubscribeProtocol(){
        controlStr = LaserConstant.SUBSCRIBE;
    }
    public SubscribeProtocol(boolean isunRegister){
        controlStr = LaserConstant.SUBSCRIBE;
        isunRegister = isunRegister;
    }


    @Override
    public String makeSvrCmdStr() {
        if(isunRegister){//撤销所有
            return "04";
        }
        if(mDevice != null)//订阅特定的设备
        {
            String id = HexUtil.cn2Hex(mDevice.deviceid);
            String pwd = HexUtil.cn2Hex(mDevice.equ_pwd);
            return "01"+id+pwd;
        }else{
            return "03";//订阅全部
        }
    }

    @Override
    public void encode(BaseLaserDate laserDate) {
        String state = laserDate.busStr.substring(2,laserDate.busStr.length());
        if(state.equals("01")){
            mState = SUCCESS;
        }else{
            mState = FAIL;
        }
    }
}
