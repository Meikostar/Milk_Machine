package socket.laserprotocol.operate;

import socket.LaserConstant;
import socket.util.HexUtil;

/**
 * Created by linquandong on 17/2/6.
 */
public class LaserLight extends Operate {
    public boolean isOpen;
    public String busId = LaserConstant.LASERLIGHT;
    public LaserLight(boolean switchSate){
        this.isOpen = switchSate;

    }
    public LaserLight(String laserData){
        encode(laserData);
    }

    @Override
    public String makeCommunicateStr() {
        String hefix = HexUtil.or(busId,"80");   // 写
        String data ="";
        if(!isOpen){
            data ="00";
        }else{
            data = "A5";
        }
        return makCommnuByHeaderData(hefix,data);
    }

    @Override
    public void encode(String laserData) {
        //TODO 解析laserData
    }

    @Override
    public String getStatus() {
        return "激光";
    }
}
