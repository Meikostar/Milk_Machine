package socket.laserprotocol.operate;

import socket.LaserConstant;
import socket.util.HexUtil;

/**
 * Created by linquandong on 17/2/21.
 * 工作模式
 */
public class WorkMode extends Operate{
    public static final int INNDER = 0;
    public static final int WIDE = 1;
    public String busId = LaserConstant.WORKMODE;
    private int modeid;//0:内控 ，1：外控
    @Override
    public String makeCommunicateStr() {
        String hefix = HexUtil.or(busId,"80"); //切换工作模式，写
        String data = "";
        if(modeid == 0){
            data = "AA";//内控
        }else{
            data = "55";//外控
        }
        return makCommnuByHeaderData(hefix,data);
    }

    public void setModeid(int modeid) {
        this.modeid = modeid;
    }

    @Override
    public void encode(String laserData) {

    }
}
