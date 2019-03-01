package udpsocket.core;

import android.os.Handler;
import android.os.Message;

import com.mykar.framework.activeandroid.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import socket.util.HexUtil;

/**
 * Created by Meikostar on 2018/6/7.
 */

public class UDPSocketBroadCast {
    private final String BROADCAST_IP = "224.224.224.225";
    private final int BROADCAST_PORT = 5000;
    private byte[] getData = new byte[1024];
    private boolean isStop = false;
    private MulticastSocket mSocket = null;
    private InetAddress address = null;
    private DatagramPacket dataPacket;
    private Thread mUDPThread = null;
    private UDPDataCallBack mCallBack = null;
    /**
     * 开始接收广播
     *
     * @param ip
     */
    public void startUDP(UDPDataCallBack mCallBack) {
        this.mCallBack = mCallBack;
        mUDPThread = new Thread(UDPRunning);
        mUDPThread.start();
    }
    /**
     * 重新启动，当接收到udp后会停掉广播，再次需要时使用reStartUDp()启动
     *
     * @param ip
     */
    public void reStartUDP() {
        Log.d("tag", "UDP is reStart!");
        mUDPThread = null;
        isStop = false;
        mUDPThread = new Thread(UDPRunning);
        mUDPThread.start();
    }
    /**
     * 停止广播
     */
    public void stopUDP() {
        isStop = true;
        mUDPThread.interrupt();
    }
    /**
     * 创建udp数据
     */
    private void CreateUDP() {
        try {
            mSocket = new MulticastSocket(BROADCAST_PORT);
            mSocket.setTimeToLive(1);// 广播生存时间0-255
            address = InetAddress.getByName(BROADCAST_IP);
            mSocket.joinGroup(address);
            dataPacket = new DatagramPacket(getData, getData.length, address,
                    BROADCAST_PORT);
            Log.d("tag", "udp is create");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Runnable UDPRunning = new Runnable() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String result = (String) msg.obj;
                mCallBack.mCallback(result);
                Log.d("tag", "handler get data:" + result);
            }
        };
        @Override
        public void run() {
            CreateUDP();
            Message msg = handler.obtainMessage();
            while (!isStop) {
                if (mSocket != null) {
                    try {
                        if(dataPacket!=null){
                            mSocket.receive(dataPacket);
                        }
                        String   mUDPData1 = HexUtil.bytes2HexString(getData);
//                        String mUDPData = new String(getData,"UTF-8");

                        /**
                         * 确定是否是这个客户端发过来的数据
                         */
                        if (mUDPData1 != null&&mUDPData1.substring(0,4).equals("AABB")) {
                            msg.obj = mUDPData1;
                            handler.sendMessage(msg);
                            isStop = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    msg.obj = "error";
                    handler.sendMessage(msg);
                }
            }
        }
    };
    /**
     * Ascii转换为字符串
     * @param value
     * @return
     */
    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }

    public interface UDPDataCallBack {
        public void mCallback(String str);
    }
}