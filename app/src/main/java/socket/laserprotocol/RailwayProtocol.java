package socket.laserprotocol;


import com.canplay.milk.util.ConfigUtils;

import socket.LaserConstant;
import socket.laserprotocol.device.LaserDevice;
import socket.util.HexUtil;

/**
 * Created by linquandong on 2017/5/24.
 * 专线连接设置
 */

public class RailwayProtocol extends Baseprotocol {
    LaserDevice mDevice;
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    public static final int CANCEL = 2;
    private int mState;

    public RailwayProtocol(LaserDevice deivce){
        controlStr = LaserConstant.RAILWAY;
        mDevice = deivce;
    }

    public RailwayProtocol(){
        controlStr = LaserConstant.RAILWAY;
    }

    public boolean isEnable(){
        return mState == SUCCESS;
    }
    public boolean isCancel(){
        return mState == CANCEL;
    }

    @Override
    public String makeSvrCmdStr() {
        if(mDevice != null)
        {
            String id = HexUtil.cn2Hex(mDevice.deviceid);
            String pwd = HexUtil.cn2Hex(mDevice.equ_pwd);
            return "01"+id+pwd;//申请专线连接
        }else{
            mDevice =  ConfigUtils.getSubcribeDevice();
            if(mDevice != null){
                String id = HexUtil.cn2Hex(mDevice.deviceid);
                String pwd = HexUtil.cn2Hex(mDevice.equ_pwd);
                return "02"+id+pwd;//申请专线连接
            }
            return "02";//撤销专线
        }
    }

    @Override
    public void encode(BaseLaserDate laserDate) {
        if(laserDate.busStr.equals("0101")){//申请专线成功
            mState = SUCCESS;
        }else if(laserDate.busStr.equals("0201")){//撤销专线成功
            mState = CANCEL;
        }else{
            mState = FAIL;
        }
    }
}
