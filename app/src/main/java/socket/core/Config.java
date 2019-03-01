package socket.core;


import com.canplay.milk.util.ConfigUtils;

/**
 * Created by neal on 2014/12/2.
 */
public class Config {
    /**
     * 服务器地址
     */
    public static String HOSTNAME = ConfigUtils.getIp();
    /**
     * 服务器端口号
     */
    public static  int PORT = ConfigUtils.getPort();
    /**
     * 连接超时时间，30 seconds
     */
    public static final long SOCKET_CONNECT_TIMEOUT = 30 * 1000L;
    
    /**
     * 最大重试次数
     */
    public static final int MAX_RETRY = 3;

    /***
     * 响应超时时间，10 seconds
     */
    public static long RESPONE_TIMEOUT = 5*1000L;
    /**
     * 长连接心跳包发送频率，40 seconds
     */
    public static final int KEEP_ALIVE_TIME_INTERVAL = 40;
    /**
     * 长连接心跳包应答超时
     */
    public static final int KEEP_ALIVE_RESPONSE_TIMEOUT = 40;
    /**
     * 心跳包 ping message
     */
    public static final String PING_MESSAGE="64abbe";//心跳

    /**
     * 局域网心跳包 ping message
     */
    public static final String PING_MESSAGE_LAN="AA BB 0E 81 68 C6 3A B7 79 AF A7 C0 A8 32 86 13 88 31 B3 BB AA";//心跳
    /**
     * 心跳包 pong message
     */
    public static final String PONG_MESSAGE="AA BB 0E 81 68 C6 3A B7 79 AF A7 C0 A8 32 86 13 88 31 B3 BB AA";

    public static String getHeart() {
        return ConfigUtils.getEnv() == ConfigUtils.WAN ? PING_MESSAGE :PING_MESSAGE_LAN;
    }
}
