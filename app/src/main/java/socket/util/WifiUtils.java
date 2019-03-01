package socket.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.canplay.milk.base.BaseApplication;
import com.mykar.framework.ApplicationHolder;

import org.litepal.LitePalApplication;

import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by linquandong on 15/8/22.
 */
public class WifiUtils {
    private  static ConnectivityManager connectivityManager;
    public WifiManager mWifiManager;
    public static WifiInfo mWifiInfo;

    private static final String DEYI_WIFI = "wifis";

    private WifiUtils() {
        mWifiManager = (WifiManager) BaseApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) BaseApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    private static WifiUtils mWifiUtils;

    public static WifiUtils shareInstance() {
        if (mWifiUtils == null) {
            //取得WifiInfo对象
            return new WifiUtils();
        }
        return mWifiUtils;
    }

    //打开wifi
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);

        }
    }


    /**
     * 获取IP
     * @param context
     * @return
     */
    public static String getIp(final Context context) {
        String ip = null;


        // mobile 3G Data Network
        android.net.NetworkInfo.State mobile = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).getState();
        // wifi
        android.net.NetworkInfo.State wifi = connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState();

        // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
        if (mobile == android.net.NetworkInfo.State.CONNECTED
                || mobile == android.net.NetworkInfo.State.CONNECTING) {
            ip =  getLocalIpAddress();
        }
        if (wifi == android.net.NetworkInfo.State.CONNECTED
                || wifi == android.net.NetworkInfo.State.CONNECTING) {
            //获取wifi服务
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //判断wifi是否开启
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip =(ipAddress & 0xFF ) + "." +
                    ((ipAddress >> 8 ) & 0xFF) + "." +
                    ((ipAddress >> 16 ) & 0xFF) + "." +
                    ( ipAddress >> 24 & 0xFF) ;
        }
        return ip;

    }

    //关闭wifi
    public void closeWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    //得到wifiInfo的所有信息
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "" : mWifiInfo.toString();
    }

    public String getBSSID() {
        return (mWifiInfo == null) ? "" : mWifiInfo.getBSSID();
    }

    public String getWifiName() {
        String ssid="";
        if(mWifiInfo!=null){
             ssid = mWifiInfo.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
        }

        return (mWifiInfo == null) ? "" : ssid;
    }

    public static int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    public static String getLocalIpAddress() {
        try {
            return int2ip(getIpAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    public String getMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iF = interfaces.nextElement();
                byte[] addr = iF.getHardwareAddress();
                if (addr == null || addr.length == 0) {
                    continue;
                }
                StringBuilder buf = new StringBuilder();
                for (byte b : addr) {
                    buf.append(String.format("%02X:", b));
                }
                if (buf.length() > 0) {
                    buf.deleteCharAt(buf.length() - 1);
                }
                return buf.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mWifiInfo.getMacAddress();
    }

    public String getWifiPwd() {
        SharedPreferences sharedPreferences = LitePalApplication.getContext().getSharedPreferences(DEYI_WIFI, 0);
        return sharedPreferences.getString(getBSSID(), "");
    }

    public void saveWifiPwd(String pwd) {
        SharedPreferences sharedPreferences = LitePalApplication.getContext().getSharedPreferences(DEYI_WIFI, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getBSSID(), pwd);
        editor.commit();
    }

    public boolean isUseable() {

        if(mWifiManager.isWifiEnabled()) {
            return true;
        }else{
            return false;
        }
    }

    public boolean isWifi(){
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isAvailable()) {
            String name = info.getTypeName();
            if(name.equals("WIFI")) {
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
