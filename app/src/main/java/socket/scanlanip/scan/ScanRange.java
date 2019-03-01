package socket.scanlanip.scan;

import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Callable;


public class ScanRange implements Callable<ArrayList<LaserAddress>> {

    private String TAG = "ScanRange";
    private long start = 0;
    private long end = 0;
    //private final int REACH_TIME_OUT = 100;
    public static int reachOutTime = 100;

    public ScanRange(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public ArrayList<LaserAddress> call() throws Exception {
        ArrayList<LaserAddress> devicesAddr = new ArrayList<LaserAddress>();
        InetAddress address = null;
        Log.d(TAG, "IP Scan from " + IpUtil.getIpFromLongUnsigned(start) + " to " + IpUtil.getIpFromLongUnsigned(end) + " start!");


        for (long i = start; i <= end; i++) {
            if (Thread.currentThread().isInterrupted()) {
                Log.e(TAG, "thread is interupted");
                return null;
            }
            String addr = IpUtil.getIpFromLongUnsigned(i);

            try {
                String name = null;

                TelnetOperator telnet = new TelnetOperator("VT220", "#");    //Windows,用VT220,否则会乱码
                name = telnet.login(addr, 8080);
                if (name != null) {
                    LaserAddress address1 = new LaserAddress(name, addr);
                    devicesAddr.add(address1);
                }
            } catch (Exception e) {
                Log.e(TAG, "IOException");
                i = end + 1; // exit loop
            }
        }
        Log.d(TAG, "IP Scan from " + IpUtil.getIpFromLongUnsigned(start) + " to " + IpUtil.getIpFromLongUnsigned(end) + "end!");
        return devicesAddr;
    }

    public void setReachOutTime(int ms) {
        if (ms > 0)
            reachOutTime = ms;
    }
}
