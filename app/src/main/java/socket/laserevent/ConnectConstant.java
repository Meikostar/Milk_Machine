package socket.laserevent;

/**
 * Created by linquandong on 17/1/5.
 */
public class ConnectConstant {
    public static final int MSGRECEI_BUS = 0; //业务接收信息
    public static final int MSGRECEI_STATE = 1; //状态信息
    public static final int CONNNECT = 2;//连接成功
    public static final int DISCONNECT = 3;//断开
    public static final int CAUGHT = 4; //异常
    public static final int FAIL = 5;//建立连接失败
    public static final int NO_NETWORK = 6;//没有网络
    public static final int DENLAY = 7;//延时重试
    public static final int DENLAYOVER = 8; //超过重试次数
}
