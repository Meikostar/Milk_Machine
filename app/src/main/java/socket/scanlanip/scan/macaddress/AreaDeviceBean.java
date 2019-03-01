package socket.scanlanip.scan.macaddress;



/**
 * Created by user on 2017/5/9.
 * 局域网设备
 */

public class AreaDeviceBean {
    private String name = "unknow";
    private String ip;
    private String mac;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
