package socket.laserprotocol;

/**
 * Created by linquandong on 16/12/10.
 * 基础的数据结构
 */
public class BaseLaserDate {

    public String controlStr;
    public String lenStr;
    public String busStr;
    public int ret;
    public String errorDesc;
    public String nameStr = "";

    public boolean identify(String idStr) {
        try {
            return controlStr.equals(idStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
