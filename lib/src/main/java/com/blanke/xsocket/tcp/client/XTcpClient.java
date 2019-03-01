package com.blanke.xsocket.tcp.client;

import com.blanke.xsocket.BaseXSocket;
import com.blanke.xsocket.tcp.client.bean.TargetInfo;
import com.blanke.xsocket.tcp.client.bean.TcpMsg;
import com.blanke.xsocket.tcp.client.listener.TcpClientListener;
import com.blanke.xsocket.tcp.client.manager.TcpClientManager;
import com.blanke.xsocket.tcp.client.state.ClientState;
import com.blanke.xsocket.utils.CharsetUtil;
import com.blanke.xsocket.utils.ExceptionUtils;
import com.blanke.xsocket.utils.XSocketLog;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * tcp客户端
 */
public class XTcpClient extends BaseXSocket {
    public static final String TAG = "XTcpClient";
    protected TargetInfo mTargetInfo;//目标ip和端口号
    protected Socket mSocket;
    protected ClientState mClientState;
    protected TcpConnConfig mTcpConnConfig;
    protected ConnectionThread mConnectionThread;
    protected SendThread mSendThread;
    protected ReceiveThread mReceiveThread;
    protected List<TcpClientListener> mTcpClientListeners;
    private LinkedBlockingQueue<TcpMsg> msgQueue;

    private XTcpClient() {
        super();
    }

    /**
     * 创建tcp连接，需要提供服务器信息
     *
     * @param targetInfo
     * @return
     */
    public static XTcpClient getTcpClient(TargetInfo targetInfo) {
        return getTcpClient(targetInfo, null);
    }

    public static XTcpClient getTcpClient(TargetInfo targetInfo, TcpConnConfig tcpConnConfig) {
        XTcpClient XTcpClient = TcpClientManager.getTcpClient(targetInfo);
        if (XTcpClient == null) {
            XTcpClient = new XTcpClient();
            XTcpClient.init(targetInfo, tcpConnConfig);
            TcpClientManager.putTcpClient(XTcpClient);
        }
        return XTcpClient;
    }

    /**
     * 根据socket创建client端，目前仅用在socketServer接受client之后
     *
     * @param socket
     * @return
     */
    public static XTcpClient getTcpClient(Socket socket, TargetInfo targetInfo) {
        return getTcpClient(socket, targetInfo, null);
    }

    public static XTcpClient getTcpClient(Socket socket, TargetInfo targetInfo, TcpConnConfig connConfig) {
        if (!socket.isConnected()) {
            ExceptionUtils.throwException("socket is closeed");
        }
        XTcpClient xTcpClient = new XTcpClient();
        xTcpClient.init(targetInfo, connConfig);
        xTcpClient.mSocket = socket;
        xTcpClient.mClientState = ClientState.Connected;
        xTcpClient.onConnectSuccess();
        return xTcpClient;
    }


    private void init(TargetInfo targetInfo, TcpConnConfig connConfig) {
        this.mTargetInfo = targetInfo;
        mClientState = ClientState.Disconnected;
        mTcpClientListeners = new ArrayList<>();
        if (mTcpConnConfig == null && connConfig == null) {
            mTcpConnConfig = new TcpConnConfig.Builder().create();
        } else if (connConfig != null) {
            mTcpConnConfig = connConfig;
        }
    }

    public synchronized TcpMsg sendMsg(String message) {
        TcpMsg msg = new TcpMsg(message, mTargetInfo, TcpMsg.MsgType.Send);
        return sendMsg(msg);
    }

    public synchronized TcpMsg sendMsg(byte[] message) {
        TcpMsg msg = new TcpMsg(message, mTargetInfo, TcpMsg.MsgType.Send);
        return sendMsg(msg);
    }

    public synchronized TcpMsg sendMsg(TcpMsg msg) {
        if (isDisconnected()) {
            XSocketLog.d(TAG, "发送消息 " + msg + "，当前没有tcp连接，先进行连接");
            connect();
        }
        boolean re = enqueueTcpMsg(msg);
        if (re) {
            return msg;
        }
        return null;
    }

    public synchronized boolean cancelMsg(TcpMsg msg) {
        return getSendThread().cancel(msg);
    }

    public synchronized boolean cancelMsg(int msgId) {
        return getSendThread().cancel(msgId);
    }

    public synchronized void connect() {
        if (!isDisconnected()) {
            XSocketLog.d(TAG, "已经连接了或正在连接");
            return;
        }
        XSocketLog.d(TAG, "tcp connecting");
        setClientState(ClientState.Connecting);//正在连接
        getConnectionThread().start();
    }

    public synchronized Socket getSocket() {
        if (mSocket == null || isDisconnected() || !mSocket.isConnected()) {
            mSocket = new Socket();
            try {
                mSocket.setSoTimeout((int) mTcpConnConfig.getReceiveTimeout());
            } catch (SocketException e) {
//                e.printStackTrace();
            }
        }
        return mSocket;
    }

    public synchronized void disconnect() {
        disconnect("手动关闭tcpclient", null);
    }

    protected synchronized void onErrorDisConnect(String msg, Exception e) {

        if (isDisconnected()) {
            return;
        }
        disconnect(msg, e);
        if (mTcpConnConfig.isReconnect()) {//重连
            connect();
        }
    }

    protected synchronized void disconnect(String msg, Exception e) {
        if (isDisconnected()) {
            return;
        }
        closeSocket();
        getConnectionThread().interrupt();
        getSendThread().interrupt();
        getReceiveThread().interrupt();
        setClientState(ClientState.Disconnected);
        notifyDisconnected(msg, e);
        XSocketLog.d(TAG, "tcp closed");
    }

    private synchronized boolean closeSocket() {
        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket=null;
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
        return true;
    }

    //连接已经连接，接下来的流程，创建发送和接受消息的线程
    private void onConnectSuccess() {
        XSocketLog.d(TAG, "tcp connect 建立成功");
        setClientState(ClientState.Connected);//标记为已连接
        getSendThread().start();
        getReceiveThread().start();
    }

    /**
     * tcp连接线程
     */
    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            try {
                int localPort = mTcpConnConfig.getLocalPort();
                if (localPort > 0) {
                    if (!getSocket().isBound()) {
                        getSocket().bind(new InetSocketAddress(localPort));
                    }
                }
                getSocket().connect(new InetSocketAddress(mTargetInfo.getIp(), mTargetInfo.getPort()),
                        (int) mTcpConnConfig.getConnTimeout());
                XSocketLog.d(TAG, "创建连接成功,target=" + mTargetInfo + ",localport=" + localPort);
            } catch (Exception e) {
                XSocketLog.d(TAG, "创建连接失败,target=" + mTargetInfo + "," + e);
                onErrorDisConnect("创建连接失败", e);
                return;
            }
            notifyConnected();
            onConnectSuccess();
        }
    }

    public boolean enqueueTcpMsg(final TcpMsg tcpMsg) {
        if (tcpMsg == null || getMsgQueue().contains(tcpMsg)) {
            return false;
        }
        try {
            getMsgQueue().put(tcpMsg);
            return true;
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
        return false;
    }

    protected LinkedBlockingQueue<TcpMsg> getMsgQueue() {
        if (msgQueue == null) {
            msgQueue = new LinkedBlockingQueue<>();
        }
        return msgQueue;
    }

    private class SendThread extends Thread {
        private TcpMsg sendingTcpMsg;

        protected SendThread setSendingTcpMsg(TcpMsg sendingTcpMsg) {
            this.sendingTcpMsg = sendingTcpMsg;
            return this;
        }

        public TcpMsg getSendingTcpMsg() {
            return this.sendingTcpMsg;
        }

        public boolean cancel(TcpMsg packet) {
            return getMsgQueue().remove(packet);
        }

        public boolean cancel(int tcpMsgID) {
            return getMsgQueue().remove(new TcpMsg(tcpMsgID));
        }

        @Override
        public void run() {
            TcpMsg msg;
            try {
                while (isConnected() && !Thread.interrupted() && (msg = getMsgQueue().take()) != null) {
                    setSendingTcpMsg(msg);//设置正在发送的
                    XSocketLog.d(TAG, "tcp sending msg=" + msg);
                    byte[] data = msg.getSourceDataBytes();
//                    byte[] data  = {(byte)0xAA,(byte)0xBB,(byte)0x0E,(byte)0x81,(byte)0x68,(byte)0xC6,(byte)0x3A,(byte)0xB7,(byte)0x79,(byte)0xAF,(byte)0xA7,(byte)0xC0,(byte)0xA8,(byte)0x32,(byte)0x86,(byte)0x13,(byte)0x88,(byte)0x31,(byte)0xB3,(byte)0xBB,(byte)0xAA};;
                    if (data == null) {//根据编码转换消息
                        data = CharsetUtil.stringToData(msg.getSourceDataString(), mTcpConnConfig.getCharsetName());
                    }
                    if (data != null && data.length > 0) {
                        try {
                            getSocket().getOutputStream().write(data);
                            getSocket().getOutputStream().flush();
                            msg.setTime();
                            notifySended(msg);
                        } catch (IOException e) {
                            e.printStackTrace();
                            onErrorDisConnect("发送消息失败", e);
                            return;
                        }
                    }
                }
            } catch (InterruptedException e) {
                onErrorDisConnect("发送消息失败", e);
                e.printStackTrace();
            }
        }
    }

    private class ReceiveThread extends Thread {
        @Override
        public void run() {
            try {
                InputStream is = getSocket().getInputStream();
                while (isConnected() && !Thread.interrupted()) {
                    byte[] result = mTcpConnConfig.getStickPackageHelper().execute(is);//粘包处理
                    if (result == null) {//报错
                        XSocketLog.d(TAG, "tcp Receive 粘包处理失败 " + Arrays.toString(result));
                        onErrorDisConnect("粘包处理中发送错误", null);
                        break;
                    }
                    XSocketLog.d(TAG, "tcp Receive 解决粘包之后的数据 " + Arrays.toString(result));
                    TcpMsg tcpMsg = new TcpMsg(result, mTargetInfo, TcpMsg.MsgType.Receive);
                    tcpMsg.setTime();
                    String msgstr = CharsetUtil.dataToString(result, mTcpConnConfig.getCharsetName());
                    tcpMsg.setSourceDataString(msgstr);
                    boolean va = mTcpConnConfig.getValidationHelper().execute(result);
                    if (!va) {
                        XSocketLog.d(TAG, "tcp Receive 数据验证失败 ");
                        notifyValidationFail(tcpMsg);//验证失败
                        continue;
                    }
                    byte[][] decodebytes = mTcpConnConfig.getDecodeHelper().execute(result, mTargetInfo, mTcpConnConfig);
                    tcpMsg.setEndDecodeData(decodebytes);
                    XSocketLog.d(TAG, "tcp Receive  succ msg= " + tcpMsg);
                    notifyReceive(tcpMsg);//notify listener
                }
            } catch (Exception e) {
                XSocketLog.d(TAG, "tcp Receive  error  " + e);
                onErrorDisConnect("接受消息错误", e);
            }
        }
    }

    protected synchronized ReceiveThread getReceiveThread() {
        if (mReceiveThread == null || !mReceiveThread.isAlive()) {
            mReceiveThread = new ReceiveThread();
        }else {

            mReceiveThread=null;
            mReceiveThread = new ReceiveThread();
        }
        return mReceiveThread;
    }

    protected synchronized SendThread getSendThread() {
        if (mSendThread == null || !mSendThread.isAlive()) {
            mSendThread = new SendThread();
        }else {

            mSendThread=null;
            mSendThread = new SendThread();
        }
        return mSendThread;
    }

    protected ConnectionThread getConnectionThread() {
        if (mConnectionThread == null || !mConnectionThread.isAlive() || mConnectionThread.isInterrupted()) {
            mConnectionThread = new ConnectionThread();
        }else {
            mConnectionThread=null;
            mConnectionThread = new ConnectionThread();
        }
        return mConnectionThread;
    }

    public ClientState getClientState() {
        return mClientState;
    }

    protected void setClientState(ClientState state) {
        if (mClientState != state) {
            mClientState = state;
        }
    }

    public boolean isDisconnected() {
        return getClientState() == ClientState.Disconnected;
    }

    public boolean isConnected() {
        return getClientState() == ClientState.Connected;
    }

    private synchronized void notifyConnected() {
        TcpClientListener l;
        listeners=null;
        listeners=new ArrayList<>();
        if(mTcpClientListeners==null||mTcpClientListeners.size()==0){
            return;
        }
//        for (TcpClientListener wl : mTcpClientListeners) {
//            listeners.add(wl);
//
//        }
        for(int i=0;i<mTcpClientListeners.size();i++){
            listeners.add(mTcpClientListeners.get(i));
        }
        for (TcpClientListener wl : listeners) {

            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onConnected(XTcpClient.this);
                }
            });
        }

    }

    private synchronized void notifyDisconnected(final String msg, final Exception e) {

        TcpClientListener l;
        listeners=null;
        listeners=new ArrayList<>();
        if(mTcpClientListeners==null||mTcpClientListeners.size()==0){
            return;
        }
//        for (TcpClientListener wl : mTcpClientListeners) {
//            listeners.add(wl);
//
//        }
        for(int i=0;i<mTcpClientListeners.size();i++){
            listeners.add(mTcpClientListeners.get(i));
        }
        for (TcpClientListener wl : listeners) {

            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onDisconnected(XTcpClient.this, msg, e);
                }
            });
        }
//        for (TcpClientListener wl : mTcpClientListeners) {
//            final TcpClientListener finalL = wl;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    finalL.onDisconnected(XTcpClient.this, msg, e);
//                }
//            });
//        }
    }


    private synchronized void notifyReceive(final TcpMsg tcpMsg) {
        TcpClientListener l;
        listeners=null;
        listeners=new ArrayList<>();
        if(mTcpClientListeners==null||mTcpClientListeners.size()==0){
            return;
        }
//        for (TcpClientListener wl : mTcpClientListeners) {
//            listeners.add(wl);
//
//        }
        for(int i=0;i<mTcpClientListeners.size();i++){
            listeners.add(mTcpClientListeners.get(i));
        }
//        if(mTcpClientListeners==null||mTcpClientListeners.size()==0){
//            return;
//        }
//        for (TcpClientListener wl : mTcpClientListeners) {
//            listeners.add(wl);
//
//        }
        for (TcpClientListener wl : listeners) {

            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(finalL!=null){
                        finalL.onReceive(XTcpClient.this, tcpMsg);
                    }

                }
            });
        }
//        for (TcpClientListener wl : mTcpClientListeners) {
//            final TcpClientListener finalL = wl;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(finalL!=null){
//                        finalL.onReceive(XTcpClient.this, tcpMsg);
//                    }
//
//                }
//            });
//        }
    }


    private List<TcpClientListener> listeners=null;
    private  synchronized void  notifySended(final TcpMsg tcpMsg) {
        TcpClientListener l;
        listeners=null;
        listeners=new ArrayList<>();

        if(mTcpClientListeners==null||mTcpClientListeners.size()==0){
            return;
        }
//        for (TcpClientListener wl : mTcpClientListeners) {
//            listeners.add(wl);
//
//        }
        for(int i=0;i<mTcpClientListeners.size();i++){
            listeners.add(mTcpClientListeners.get(i));
        }

        for (TcpClientListener wl : listeners) {
            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onSended(XTcpClient.this, tcpMsg);
                }
            });
        }
    }

    private synchronized void notifyValidationFail(final TcpMsg tcpMsg) {
        TcpClientListener l;
        listeners=null;
        listeners=new ArrayList<>();
        if(mTcpClientListeners==null||mTcpClientListeners.size()==0){
            return;
        }
//        for (TcpClientListener wl : mTcpClientListeners) {
//            listeners.add(wl);
//
//        }
        for(int i=0;i<mTcpClientListeners.size();i++){
            listeners.add(mTcpClientListeners.get(i));
        }
        for (TcpClientListener wl : listeners) {

            final TcpClientListener finalL = wl;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finalL.onValidationFail(XTcpClient.this, tcpMsg);
                }
            });
        }

    }

    public TargetInfo getTargetInfo() {
        return mTargetInfo;
    }

    public void addTcpClientListener(TcpClientListener listener) {
        if (mTcpClientListeners.contains(listener)) {
            return;
        }
        mTcpClientListeners.add(listener);
    }

    public void removeTcpClientListener(TcpClientListener listener) {
        mTcpClientListeners.remove(listener);
    }

    public void config(TcpConnConfig tcpConnConfig) {
        mTcpConnConfig = tcpConnConfig;
    }

    @Override
    public String toString() {
        return "XTcpClient{" +
                "mTargetInfo=" + mTargetInfo + ",state=" + mClientState + ",isconnect=" + isConnected() +
                '}';
    }
}
