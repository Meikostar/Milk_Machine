package socket.scanlanip.scan;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import apache.mina.core.future.ConnectFuture;
import apache.mina.core.service.IoHandler;
import apache.mina.core.session.IdleStatus;
import apache.mina.core.session.IoSession;
import apache.mina.filter.codec.ProtocolCodecFilter;
import apache.mina.filter.codec.textline.TextLineCodecFactory;
import apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import socket.scanlanip.scan.macaddress.AllUitls;
import socket.scanlanip.scan.macaddress.AreaDeviceBean;
import socket.util.HandlerUtils;
import socket.util.HexUtil;
import socket.util.WifiUtils;

public class ArpEgScanIp {

    final static String TAG = "ScanIp";
    private static final long CONNECT_TIMEOUT = 3000;
    private int numOfThread = 255;
    private int maxThread = 20;
    private static final int MIN_CIDR = 24;
    String scanIpAddr;
    String scanMask;
    long ip_start; // unsigned long
    long ip_end;   // unsigned long
    int cidr;
    public boolean isStop;
    private ExecutorService executor;
    HashMap<String, String> deviceMap = new HashMap<>();
    private Context mContext;
    private String mLocalIP;
    private int mSum;
    private ArrayList<AreaDeviceBean> mBeanlist;
    public final static String LASERMAC = "28:f3:66";

    public ArpEgScanIp(Context context) {
        mContext = context;
        executor = null;
        cidr = MIN_CIDR; // default
        mLocalIP = AllUitls.getIPAddressStr(mContext);
    }


    public void startScan() {
        getIpMac();
    }

    public int getSumValue() {
        return mSum;
    }

    private void getIpMac() {

        Observable.create(new Observable.OnSubscribe<List>() {
            @Override
            public void call(Subscriber<? super List> subscriber) {
                AllUitls.initAreaIp(mContext);
                List<AreaDeviceBean> beans = new ArrayList<>();
                int sum = 0;
                while (beans.size() == 0 && sum < 8) {
                    beans.addAll(AllUitls.getAllCacheMac(mLocalIP));
                    SystemClock.sleep(beans.size() > 0 ? 0 : 1000);
                    sum++;
                }
                subscriber.onNext(beans);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List list) {
                        mBeanlist = (ArrayList<AreaDeviceBean>) list;
                        socketConnect(mBeanlist);
                    }
                });
    }


    private void socketConnect(ArrayList<AreaDeviceBean> list) {
        if (executor == null) {
            executor = Executors.newFixedThreadPool(maxThread);
        }
        isStop = false;
        mSum = list.size();
        for (int i = 0; i < mSum; i++) {
            AreaDeviceBean areaDeviceBean = list.get(i);
            executor.submit(new ClientRunable(areaDeviceBean.getIp(), areaDeviceBean.getMac()));
        }
    }

    private void getIps(Context contxt) {
        scanIpAddr = Network.getIpAddr(contxt);
        scanMask = Network.getMaskAddr(contxt);

        // Detect start and end addr
        cidr = IpUtil.IpToCidr(scanMask);
        long network_ip = IpUtil.getUnsignedLongFromIp(scanIpAddr);
        int shift = (32 - cidr);
        if (cidr < 31) {
            ip_start = (network_ip >> shift << shift) + 1;
            ip_end = (ip_start | ((1 << shift) - 1)) - 1;
        } else {
            ip_start = (network_ip >> shift << shift);
            ip_end = (ip_start | ((1 << shift) - 1));
        }
    }

    private List<long[]> splitIps(long start, long end, int partion) {
//        if (partion > Math.abs((end - start)) || (partion > 10) || (partion < 1)) {
//            Log.d(TAG, "partion Limited from 1 to 10");
//            return null;
//        }
        List<long[]> rangeList = new ArrayList<long[]>();
        long interval = Math.abs((end - start) / partion);
        long nextStart = start;
        for (int i = 0; i < partion; i++) {
            long[] subRange = new long[2];
            subRange[0] = nextStart;
            if (i != partion - 1)
                subRange[1] = nextStart + interval - 1;
            else
                subRange[1] = end;
            rangeList.add(subRange);
            nextStart = subRange[1] + 1;
        }
        return rangeList;
    }

    public void stop() {
        isStop = true;
    }

    /**
     * shutdown调用后，不可以再submit新的task，已经submit的将继续执行。
     * shutdownNow试图停止当前正执行的task，并返回尚未执行的task的list
     */
    public void destroy() {
        isStop = true;
        if (executor != null) {
//            executor.shutdown(); // Disable new tasks from being submitted
            executor.shutdownNow(); // Cancel currently executing tasks
        }
//        try {
//                // Wait a while for existing tasks to terminate
//                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
//                    executor.shutdownNow(); // Cancel currently executing tasks
//                    // Wait a while for tasks to respond to being cancelled
//                    if (!executor.awaitTermination(60, TimeUnit.SECONDS))
//                        System.err.println("Pool did not terminate");
//                }
//            } catch (InterruptedException ie) {
//                // (Re-)Cancel if current thread also interrupted
//                executor.shutdownNow();
//                // Preserve interrupt status
//                Thread.currentThread().interrupt();
//            }
    }

    public class ClientRunable implements Runnable {
        private String ip;
        private String mac;

        public ClientRunable(String start, String end) {
            this.ip = start;
            this.mac = end;
        }

        @Override
        public void run() {
            if (isStop || !WifiUtils.shareInstance().isWifi()) {
                return;
            }
            if (!mac.startsWith(LASERMAC)) {
                sendAsProgress(ip);
                return;
            }
            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
            connector.setHandler(new ClientHandler(ip));
            connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
            IoSession session = null;
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        ip, 8080));
                future.awaitUninterruptibly();
                session = future.getSession();
            } catch (Exception e) {
                Log.d(TAG, "ThreadName:" + Thread.currentThread().getName() + "  " + ip + "==>Failed to connect.");
                sendAsProgress(ip);
            }
            if (session != null) {
                // wait until the summation is done
                session.getCloseFuture().awaitUninterruptibly();
            }
            connector.dispose();


            Log.d(TAG, "IP Scan from " + ip + "end!");
        }
    }

    private void sendAsProgress(final String ip) {
        HandlerUtils.postOnMain(new Runnable() {
            @Override
            public void run() {
                LaserAddress address = new LaserAddress(null, ip);
                EventBus.getDefault().post(address);
            }
        });
    }

    public class ClientHandler implements IoHandler {
        public String ip;

        public ClientHandler(String ip) {
            this.ip = ip;
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {

        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
//            session.getConfig().setBothIdleTime(180);//set timeout seconds, must

        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {

        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
//            session.close(true);//timeout now, close it
        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            session.close(true);
            Log.e(TAG, "caught");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            String msg = (String) message;
            if (msg != null) {
                final String devname = HexUtil.hexStr2CN(msg.substring(12, 12 + 16));
                session.close(true);
                Log.d(TAG, "devname:" + devname + "IP:" + ip);
                HandlerUtils.postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        LaserAddress address = new LaserAddress(devname, ip);
                        EventBus.getDefault().post(address);
                    }
                });
            }
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {

        }

        @Override
        public void inputClosed(IoSession session) throws Exception {
            if (session != null && session.isConnected()) {
                session.close(false);
            }
            Log.e(TAG, "close");
        }
    }
}
