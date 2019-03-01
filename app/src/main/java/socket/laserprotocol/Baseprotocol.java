package socket.laserprotocol;

import java.io.Serializable;

import socket.util.HexUtil;

/**
 * Created by linquandong on 16/12/5.
 */
public class Baseprotocol implements Serializable {
    public String controlStr;//控制符
    public String busStr;
    public int length;
    private String name;
    /**
     * 拼接激光器的数据
     * 局域网
     */
    public String makeCommunicateStr(){
        return "";
    }

    public String makeSvrCmdStr(){
        return "";
    }

    public void setProStr(String lenStr, String busStr, String nameStr){
        if(lenStr != null){
            this.length = Integer.parseInt(lenStr,16);
        }
        this.busStr = busStr;
        this.name = nameStr;
    }

    public void encode(BaseLaserDate laserDate) {
        setProStr(laserDate.lenStr,laserDate.busStr,laserDate.nameStr);
    }

    public String getName()
    {
        try {
            return new String(HexUtil.hexStringToBytes(name), "GBK");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    protected String makCommnuByHeaderData(String hefix, String data) {
        int len = 0;
        if(!data.isEmpty()){
            len=  data.length()/2;//数据长度（字节为单位所以除与2）
        }
        String length = String.format("%02x",len);
        return hefix+length+data;
    }
}
