package socket.laserprotocol.operate;

import socket.LaserConstant;
import socket.util.HexUtil;

/**
 * Created by linquandong on 17/2/6.
 */
public class RemoveFault extends Operate {
    public String busId = LaserConstant.REMOVEFAULT;
    public RemoveFault(){

    }
    public RemoveFault(String laserData){
        encode(laserData);
    }

    @Override
    public String makeCommunicateStr() {
        String hefix = HexUtil.or(busId,"80");   //开红光命令 写
        String data ="55";
        return makCommnuByHeaderData(hefix,data);
    }

    @Override
    public void encode(String laserData) {
        //TODO 解析laserData
    }

    @Override
    public String getStatus() {
        return "解除故障";
    }
}
