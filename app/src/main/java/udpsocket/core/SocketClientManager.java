package udpsocket.core;

import android.os.Handler;
import android.os.Message;

import com.mykar.framework.activeandroid.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

import socket.util.HexUtil;

/**
 * Created by Meikostar on 2018/6/7.
 */

public class SocketClientManager {
    /**
     * 单例对象模式，不同的Activity共享同一个ScoketClientMgr
     */
    private static SocketClientManager instance = null;
    private Socket clientSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private String serverIP = "";
    private int port = 0;
    public boolean isStop = false;
    private SocketClientCallBack callBack;

    public synchronized static SocketClientManager getInstance() {
        if (instance == null) {
            instance = new SocketClientManager();
        }
        return instance;
    }

    /**
     * 通过这方法回调数据
     *
     * @param callBack
     */
    public void getSocketMessage(SocketClientCallBack callBack) {
        this.callBack = callBack;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            callBack.callBack(msg.what);
        }
    };

    /**
     * 创建客户端
     *
     * @param serverIP
     * @param port
     */
    public void startClientScoket(String serverIP, int port) {
        this.serverIP = serverIP;
        this.port = port;
        isStop = false;
        this.clientSocket = null;
        this.outStream = null;
        this.inStream = null;
        new Thread(runSocket).start();
        Log.d("tag", "clientSocket is create");
    }

    private Runnable runSocket = new Runnable() {
        @Override
        public void run() {
            if (clientSocket == null) {
                try {
                    clientSocket = new Socket(serverIP, port);
                    outStream = clientSocket.getOutputStream();
                    inStream = clientSocket.getInputStream();
                    new Thread(runHeartbeat).start();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    /**
     * 心跳测试线程
     */
    private Runnable runHeartbeat = new Runnable() {
        @Override
        public void run() {
            while (!isStop) {
                SendMessage("AA BB 0E 81 68 C6 3A B7 79 AF A7 C0 A8 32 86 13 88 31 B3 BB AA");
                byte[] buf = new byte[10240];
                try {
                    inStream.read(buf);// 读取服务器端数据
                    String res = new String(buf, 0, buf.length, "utf-8").trim();
                    Log.d("tag", "return :" + res);
                    if ("".equals(res)) {// 当服务器接收空包时说明断开了
                        isStop = true;
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;// 与服务器断开
                        mHandler.sendMessage(msg);// 知道断开后发送消息
                    } else {
                        Thread.sleep(5 * 1000);// 正常
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    /**
     * 心跳测试线程
     */
    private Runnable sendRun = new Runnable() {
        @Override
        public void run() {
            while (!isStop) {
                SendMessage(content);
                byte[] buf = new byte[10240];
                try {
                    inStream.read(buf);// 读取服务器端数据
                    String res = new String(buf, 0, buf.length, "utf-8").trim();
                    Log.d("tag", "return :" + res);
                    if ("".equals(res)) {// 当服务器接收空包时说明断开了
                        isStop = true;
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;// 与服务器断开
                        mHandler.sendMessage(msg);// 知道断开后发送消息
                    } else {
                        Thread.sleep(10 * 1000);// 正常
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * 客户端正常退出调用这个方法
     */
    public void clientQuit() {
        SendMessage("IHAVEQUIT");
    }

    /**
     * 发送数据
     *
     * @param strMsg
     * @return
     */
    private static String content;

    public boolean SendMessage(final String strMsg) {
        if (clientSocket == null) {
            return false;
        }

        String msg = HexUtil.encodeHexStr(strMsg.getBytes());
        byte[] msgBuffer = null;
        try {
            msgBuffer = msg.getBytes("UTF-8");
            outStream.write(msgBuffer);
            outStream.flush();
            Log.d("tag", "send message is:" + msg);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }

    public void Close() {
        CloseSocket();
    }

    private void CloseSocket() {
        isStop = true;
        try {
            if (clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface SocketClientCallBack {
        public void callBack(int what);
    }
}
