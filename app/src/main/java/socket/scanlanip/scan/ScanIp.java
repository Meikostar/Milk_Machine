package socket.scanlanip.scan;

import android.content.Context;
import android.util.Log;

import apache.mina.core.future.ConnectFuture;
import apache.mina.core.service.IoHandler;
import apache.mina.core.session.IdleStatus;
import apache.mina.core.session.IoSession;
import apache.mina.filter.codec.ProtocolCodecFilter;
import apache.mina.filter.codec.textline.TextLineCodecFactory;
import apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import socket.util.HandlerUtils;
import socket.util.HexUtil;

public class ScanIp {

    final static String TAG = "ScanIp";
    //private static final int NUM_THREADS = 3;
    private int numOfThread = 10;
    private int scanTimeout = 100;
    private static final int TOTAL_TIMEOUT = 60000;
    private static final int MIN_CIDR = 24;
    String scanIpAddr;
    String scanMask;
    long ip_start; // unsigned long
    long ip_end;   // unsigned long
    int cidr;
    private ArrayList<InetAddress> ipList;
    private ExecutorService executor;
    List<Future<ArrayList<LaserAddress>>> returnList;
    HashMap<String,String> deviceMap = new HashMap<>();

    public ScanIp() {
        ipList = null;
        executor = null;
        cidr = MIN_CIDR; // default
    }

    public boolean setNumThread(int i) {
        numOfThread = i;
        return true;
    }

    public void startScan2(Context contxt) {
        getIps(contxt); // caculate start and end ip from network info
        List<long[]> subRanges = splitIps(ip_start, ip_end, numOfThread);
        executor = Executors.newFixedThreadPool(numOfThread);
        for (int i = 0; i < numOfThread; i++) {
            long[] range = subRanges.get(i);
            executor.submit(new ClientRunable(range[0],range[1]));
        }
    }

    public List<LaserAddress> startScan(Context contxt) {
        getIps(contxt); // caculate start and end ip from network info

        if (cidr < 24) {
            return null;
        } else {
            executor = Executors.newFixedThreadPool(numOfThread);
            returnList = new ArrayList<Future<ArrayList<LaserAddress>>>();
            List<LaserAddress> ips = new ArrayList<LaserAddress>();

            Log.d(TAG, "ScanIp start scan");
            List<long[]> subRanges = splitIps(ip_start, ip_end, numOfThread);
            if (null == subRanges)
                return null;

            List<Callable<ArrayList<LaserAddress>>> rangeScanPool = new ArrayList<Callable<ArrayList<LaserAddress>>>();

            for (int i = 0; i < numOfThread; i++) {
                long[] range = subRanges.get(i);
                ScanRange subscan = new ScanRange(range[0], range[1]);
                subscan.setReachOutTime(scanTimeout);
                rangeScanPool.add(subscan);
                Future<ArrayList<LaserAddress>> scanTask = executor.submit(rangeScanPool.get(i));
                if (scanTask != null) // if not find could return null
                    returnList.add(scanTask);
            }

            for (Future<ArrayList<LaserAddress>> future : returnList) {
                try {
                    Log.d(TAG, "subList = future.get()");
                    ArrayList<LaserAddress> subList = future.get();
                    for (int i = 0; i < subList.size(); i++)
                        ips.add(subList.get(i));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "ScanIp end scan");

            try {
                executor.shutdown();
                if (!executor.awaitTermination(TOTAL_TIMEOUT, TimeUnit.MILLISECONDS)) { //timeout
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
                //e.printStackTrace();
            }
            return ips;
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
        if (partion > Math.abs((end - start)) || (partion > 10) || (partion < 1)) {
            Log.d(TAG, "partion Limited from 1 to 10");
            return null;
        }

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

    public void onCancell() {
        if (null != returnList) {
            for (int i = 0; i < returnList.size(); i++) {
                returnList.get(i).cancel(true);
            }
        }
    }

    public void setSearchParam(int thread, int timeout) {
        if (thread >= 1)
            numOfThread = thread;
        if (timeout > 0)
            scanTimeout = timeout;

        Log.d(TAG, "setSearchParam thead = " + Integer.toString(thread) + "timeout = " + Integer.toString(timeout));

    }

    public class ClientRunable implements Runnable {


        private long start = 0;
        private long end = 0;

        public ClientRunable(long start,long end){
            this.start = start;
            this.end = end;
        }
        @Override
        public void run() {
            Log.d(TAG, "IP Scan from " + IpUtil.getIpFromLongUnsigned(start) + " to " + IpUtil.getIpFromLongUnsigned(end) + " start!");
            for (long i = start;i<=end;i++){
                String ip = IpUtil.getIpFromLongUnsigned(i);
                NioSocketConnector connector = new NioSocketConnector();
                connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
                connector.setHandler(new ClientHandler(ip));
                connector.setConnectTimeoutMillis(3 * 1000);
                IoSession session = null;
                for (;;) {
                    try {
                        ConnectFuture future = connector.connect(new InetSocketAddress(
                                ip, 8080));

                        future.awaitUninterruptibly();
                        session = future.getSession();
                        break;
                    } catch (Exception e) {
                        Log.d(TAG, "ThreadName:"+ Thread.currentThread().getName()+"  "+ip+"==>Failed to connect.");
//                        e.printStackTrace();
                        break;

                    }
                }
                if(session != null){
                    // wait until the summation is done
                    session.getCloseFuture().awaitUninterruptibly();
                }
                connector.dispose();
            }
            Log.d(TAG, "IP Scan from " + IpUtil.getIpFromLongUnsigned(start) + " to " + IpUtil.getIpFromLongUnsigned(end) + "end!");
        }
    }

    public class ClientHandler implements IoHandler {
        public String ip;

        public ClientHandler(String ip){
            this.ip = ip;
        }
        @Override
        public void sessionCreated(IoSession session) throws Exception {

        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {

        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {

        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

        }

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            Log.e(TAG, "caught");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            String msg = (String) message;
            if(msg != null){
                final String devname = HexUtil.hexStr2CN(msg.substring(12,12+16));
                if(deviceMap.get(devname) == null){
                    deviceMap.put(devname,devname);
                    HandlerUtils.postOnMain(new Runnable() {
                        @Override
                        public void run() {
                            LaserAddress address = new LaserAddress(devname,ip);
                            EventBus.getDefault().post(address);
                        }
                    });
                }
            }
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {

        }

        @Override
        public void inputClosed(IoSession session) throws Exception {
            Log.e(TAG, "close");
        }

    }

    public class SubnetMaskException extends Exception {
        private static final long serialVersionUID = 4416485006956521438L;
        private String errorCode = "SubnetMask_Range_Too_Broad_Error";

        public SubnetMaskException() {
        }

        public SubnetMaskException(String message, String errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public String getErrorCode() {
            return errorCode;
        }
    }
}
