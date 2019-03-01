package socket.laserprotocol.operate;

import socket.LaserConstant;
import socket.util.HexUtil;

/**
 * Created by linquandong on 17/2/6.
 * 内控设定值
 */
public class InnerSet extends Operate {
    public String busId = LaserConstant.INNERSET;
    public int progress;
    public int unint = 100;
    public InnerSet(){

    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public InnerSet(String laserData){
        encode(laserData);
    }

    @Override
    public String makeCommunicateStr() {
        String hefix = HexUtil.or(busId,"80");   // 写
        String data = String.format("%04x",progress*unint);
        return makCommnuByHeaderData(hefix,data);
    }

    @Override
    public void encode(String laserData) {
        //TODO 解析laserData
    }

    @Override
    public String getStatus() {
        return "";
    }
}
