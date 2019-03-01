package socket.laserprotocol.operate;

import java.io.Serializable;

/**
 * Created by linquandong on 17/2/6.
 */
public abstract class Operate implements Serializable {

    public String busId ="";
    public abstract String makeCommunicateStr();

    public abstract void encode(String laserData);

    protected String makCommnuByHeaderData(String hefix, String data) {
        int len = 0;
        if(!data.isEmpty()){
            len=  data.length()/2;//数据长度（字节为单位所以除与2）
        }
        String length = String.format("%02x",len);
        return hefix+length+data;
    }

    public String getStatus() {
        return "";
    }
}
