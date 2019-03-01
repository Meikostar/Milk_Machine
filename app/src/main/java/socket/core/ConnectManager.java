package socket.core;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;


import com.canplay.medical.R;
import com.canplay.milk.util.ConfigUtils;
import com.canplay.milk.util.FR;

import apache.mina.core.future.ConnectFuture;
import apache.mina.core.future.WriteFuture;
import apache.mina.core.service.IoHandlerAdapter;
import apache.mina.core.session.IdleStatus;
import apache.mina.core.session.IoSession;
import apache.mina.filter.codec.ProtocolCodecFactory;
import apache.mina.filter.codec.ProtocolCodecFilter;
import apache.mina.filter.codec.textline.TextLineCodecFactory;
import apache.mina.filter.keepalive.KeepAliveFilter;
import apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import de.greenrobot.event.EventBus;

import me.guhy.swiperefresh.Utils.Log;
import socket.Decode.SvrCommnuManager;
import socket.laserevent.ConnectConstant;
import socket.laserevent.ConnectEvent;
import socket.laserprotocol.BaseLaserDate;
import socket.util.HandlerUtils;
import socket.util.HexUtil;
import socket.util.NetworkUtil;


/**
 * Created by neal on 2014/12/2.
 */
public class ConnectManager {

    private ConcurrentLinkedQueue<Object> msgQueue = new ConcurrentLinkedQueue<Object>();
    private static ConnectManager connectManager;

    private NioSocketConnector connector;

    private ConnectFuture connectFuture;

    private IoSession session;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private MsgHander msgHandler;

    public static final int ERROR_CONNECT = 1;
    public static final int MSG_SEND = 0;
    private static final int MSG_SEND_TIMEOUT = 2;
    private static final int MSG_RESPONE = 3;

    private HandlerThread msgThread;
    private ConnectEvent eventConnect = new ConnectEvent();
    //是否断开过链接
    private Boolean railwayhasBroken = false;

    public Boolean getRailwayhasBroken() {
        return railwayhasBroken;
    }

    public void setRailwayhasBroken(Boolean railwayhasBroken) {
        this.railwayhasBroken = railwayhasBroken;
    }

    public ConnectEvent getEventConnect() {
        return eventConnect;
    }

    public ConnectManager() {
        initBackHandThread();
    }

    public static ConnectManager getInstance() {
        if (connectManager == null) {
            connectManager = new ConnectManager();

        }
        return connectManager;
    }

    private void initBackHandThread() {
        msgThread = new HandlerThread("eagle.ConnectManager");
        msgThread.start();
        msgHandler = new MsgHander(msgThread.getLooper());

    }

    public class MsgHander extends Handler {
        public int retryNum = 0;

        public MsgHander(Looper looper) {
            super(looper);
        }

        public void initRetryNum(){
            retryNum = 0;
        }
        @Override
        public void handleMessage(Message msg) {

            if (!isConnect()) {
                handConnect(ConnectConstant.DISCONNECT, FR.str(R.string.laser_disconnect));
                cancelAll();
                return;
            }
            switch (msg.what) {
                case MSG_SEND_TIMEOUT:
                    retryNum++;
                    if (retryNum == Config.MAX_RETRY) {//认为连接失败
                        retryNum = 0;
                        cancelAll();
                        handConnect(ConnectConstant.DENLAY, "" + FR.str(R.string.laser_netDelay) + retryNum);
//                            handConnect(ConnectConstant.DENLAYOVER,"连接超时");
                    }
                    doSendMessage();
                    break;
                case MSG_SEND:
                    retryNum = 0;
                    doSendMessage();
                    break;
            }
        }
    }

    public void cancelAll() {
        clearHander();
        msgQueue.clear();
    }

    private void clearHander() {
        msgHandler.removeCallbacksAndMessages(null);
        msgHandler.initRetryNum();
    }

    private void handFail(BaseLaserDate data) {
//        SvrCommnuManager.shareInstance().disPatchFailData(data);
        if (SvrCommnuManager.shareInstance().shouldSendmsg(data)) {
            //删除之前，去掉延时消息
//            msgHandler.removeMessages(MSG_SEND_TIMEOUT);
            //检查队列中的下个信息进行发送
//            msgHandler.sendEmptyMessage(MSG_SEND_TIMEOUT);
        }
    }

    private void handSuccess(BaseLaserDate data) {
        SvrCommnuManager.shareInstance().disPatchData(data, 0);
        //不是广播，移除成功的操作，进队列的下个操作
        if (SvrCommnuManager.shareInstance().shouldSendmsg(data)) {
            //检查队列中的下个信息进行发送
            if (msgQueue.poll() != null) {
                msgHandler.sendEmptyMessage(MSG_SEND);
            }
        }
    }


    public void setPushEventListener(SocketEventListener socketEventListener) {
        if (connector != null && connector.getHandler() != null) {
            if (connector.getHandler() instanceof ClientSessionHandler) {
                ((ClientSessionHandler) connector.getHandler()).setSocketEventListener(socketEventListener);
            }
        }
    }


    private void open() {

        if (connector != null) {
            return;
        }
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(Config.SOCKET_CONNECT_TIMEOUT);
        connector.setHandler(new SessionHandler());
//        connector.getFilterChain().addLast("codec",new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));

        //当互联网才需要加心跳
//       if (ConfigUtils.getEnv() == ConfigUtils.WAN) {
            connector.getFilterChain().addLast("keepalive", new KeepAliveFilter
                    (new ClientKeepAliveMessageFactoryImp(), IdleStatus.WRITER_IDLE,
                            KeepAliveRequestTimeoutHandler.DEAF_SPEAKER, Config.KEEP_ALIVE_TIME_INTERVAL, Config.KEEP_ALIVE_RESPONSE_TIMEOUT));

    }

    public void setProtocolCodecFilter(ProtocolCodecFactory factory) {
        if (connector == null) {
            return;
        }
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(factory));
    }


    /**
     * 开始连接
     *
     * @return
     */
    public void startConnect() {
        if (!NetworkUtil.isNetworkConnect()) {
            return;
        }
        if (connector == null) {
            open();
        } else if (connector.isActive() && connectFuture != null && connectFuture.isConnected() && session != null && session.isConnected()) {
            handConnect(ConnectConstant.CONNNECT, "连接成功");
            return;
        }
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    System.out.println("manager connecting" + android.os.Process.myPid() + '-' + Thread.currentThread().getName());
                    connectFuture = connector.connect(new InetSocketAddress(
                            Config.HOSTNAME, Config.PORT));
                    //阻塞直到连接建立,因为我们后面要使用连接成功之后创建的Session对象来进行写数据的操作
                    connectFuture.awaitUninterruptibly();
                    session = connectFuture.getSession();
                    System.out.println("manager connect" + android.os.Process.myPid() + '-' + android.os.Process.myTid());
                } catch (Exception e) {
                    e.printStackTrace();
                    if (connector.getHandler() != null) {
                        connector.getHandler().exceptionCaught(null, e);
                    }
                    return false;
                }
                return true;
            }
        });

        executorService.submit(futureTask);
    }


    public boolean sendMessage(ClientPushMessage clientPushMessage) {
        if (session == null || !session.isConnected()) {
            return false;
        }
        WriteFuture writeFuture = session.write(clientPushMessage);
        if (writeFuture == null) {
            return false;
        }
        writeFuture.awaitUninterruptibly();
        if (writeFuture.isWritten()) {
            return true;
        } else {
            return false;
        }
    }

    public void sendMessage(Object message) {
        if (session == null || !session.isConnected()) {
            msgHandler.sendEmptyMessage(ERROR_CONNECT);
            return;
        }
        if (msgQueue.isEmpty()) {
            msgQueue.add(message);
            msgHandler.sendEmptyMessage(MSG_SEND);
        } else {
            msgQueue.add(message);
        }

    }

    private void doSendMessage() {
        //删除之前的延时消息
        msgHandler.removeCallbacksAndMessages(null);
        if (!msgQueue.isEmpty()) {
            doSendMessage(msgQueue.peek());
            msgHandler.sendEmptyMessageDelayed(MSG_SEND_TIMEOUT, Config.RESPONE_TIMEOUT);
        }
    }

    private boolean doSendMessage(Object message) {
        Log.i("doSendMessage", "Threaid:" + Thread.currentThread().getId() + Thread.currentThread().getName() + "msg:" + message);
        /**
         * 发送时需要回车换行，这里加上回车就好，TextLineCodecFactory 会自动加上换行符
         */
        byte[] data  = {(byte)0xAA,(byte)0xBB,(byte)0x0E,(byte)0x81,(byte)0x68,(byte)0xC6,(byte)0x3A,(byte)0xB7,(byte)0x79,(byte)0xAF,(byte)0xA7,(byte)0xC0,(byte)0xA8,(byte)0x32,(byte)0x86,(byte)0x13,(byte)0x88,(byte)0x31,(byte)0xB3,(byte)0xBB,(byte)0xAA};
        WriteFuture writeFuture = session.write(data + "\r");
        if (writeFuture == null) {
            return false;
        }
        writeFuture.awaitUninterruptibly();
        if (writeFuture.isWritten()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 关闭连接
     */
    public void disConnect() {
        cancelAll();
        if (session != null && session.isConnected()) {
            session.close(true);
        }
        if (connectFuture != null && connectFuture.isConnected()) {
            connectFuture.cancel();
        }
        if (connector != null && !connector.isDisposed()) {
            connector.dispose();
        }
        connector = null;
    }


    public boolean isConnect() {
        if (session != null && session.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public class SessionHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
            Log.i("Egservice","Egservice create" + android.os.Process.myPid() + '-' + android.os.Process.myTid());
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
//            handConnect(ConnectConstant.CONNNECT, "连接成功");
            String msg = HexUtil.encodeHexStr("AA BB 0E 81 68 C6 3A B7 79 AF A7 C0 A8 32 86 13 88 31 B3 BB AA".getBytes());

            sendMessage(msg);
            Log.i("Egservice","Egservice open" + android.os.Process.myPid() + '-' + android.os.Process.myTid());

        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            startConnect();
            Log.i("Egservice","Egservice closed" + android.os.Process.myPid() + '-' + android.os.Process.myTid());

        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            super.exceptionCaught(session, cause);
            handConnect(ConnectConstant.CAUGHT, cause.getMessage());
            //断开连接
            disConnect();
            Log.i("Egservice","Egservice Caught" + cause.toString());

        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);
            String msg = "";
            if (message instanceof String) {
                msg = (String) message;
                BaseLaserDate data = SvrCommnuManager.shareInstance().encode(msg);
                if (data.ret == 0) {
                    handSuccess(data);
                } else {
                    handFail(data);
                }
            }
            Log.i("EgserviceReceived", "Threaid:" + Thread.currentThread().getId() + Thread.currentThread().getName() + "message:" + msg);

        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
            if (message instanceof String) {
                String msg = (String) message;
                Log.i("EgserviceSend", "Threaid:" + Thread.currentThread().getId() + Thread.currentThread().getName() + " msg:" + msg);

            }

        }

        @Override
        public void inputClosed(IoSession session) throws Exception {
//            handConnect(ConnectConstant.DISCONNECT, "连接断开"); 有可能进入该回调多次
            //断开连接
            disConnect();
            System.out.println("Egservice inputClosed" + "");
        }
    }

    private void handConnect(final int connnect, final String msg) {
        /***
         * 记录是否断开过连接，如果断开过需要重新进行专线链接
         */
        if (eventConnect.rescode != ConnectConstant.CONNNECT) {
            setRailwayhasBroken(true);
        }

        if (eventConnect.rescode == connnect) {
            return;
        }
        HandlerUtils.postOnMain(new Runnable() {
            @Override
            public void run() {
                eventConnect.rescode = connnect;
                eventConnect.msg = msg;
                EventBus.getDefault().post(eventConnect);
            }
        });

    }
}
