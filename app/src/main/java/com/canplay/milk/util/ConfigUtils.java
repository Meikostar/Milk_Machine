package com.canplay.milk.util;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import com.canplay.milk.base.BaseApplication;

import socket.Decode.SvrCommnuManager;
import socket.core.Config;

import socket.laserprotocol.device.LaserDevice;
import socket.util.HexUtil;

public class ConfigUtils {
    //String
    public static final String USEERINFO = "deviceconfig";

    private final static String IP = "ip";
    private final static String PORT = "port";
    private final static String DEVICEPW = "devicepassword";
    private final static String ENV = "env";
    public static final int LAN = 0;//局域网
    public static final int WAN = 1;//互联网

    public final static String DEVICE_ID = "deviceid";
    public final static String DEVICE_PASSWORD = "devicepw";
    private static String NETMAC = "netmac";


    private static SharedPreferences shared;
    private static Editor editor;

    static {
        shared = BaseApplication.getInstance().getSharedPreferences(USEERINFO, 0);
        editor = shared.edit();
    }

    public static void switchNetEnv() {
        setAddress("139.196.45.4", 65000);
//        setAddress("192.168.1.170", 65000);
        ConfigUtils.setPasswd("12345678");
        ConfigUtils.setEnv(ConfigUtils.WAN);
    }

    public static void setAddress(String ipstr, int port) {
        Config.HOSTNAME = ipstr;
        Config.PORT = port;
        editor.putString(IP, ipstr);
        editor.putInt(PORT, port);
        editor.commit();
    }


    public static String getIp() {
        return shared.getString(IP, "192.168.50.133");
    }

    public static int getPort() {
        return shared.getInt(PORT, 65000);
    }

    public static void setPasswd(String passwd) {
        editor.putString(DEVICEPW, passwd);
        editor.commit();
    }

    public static String getPasswd() {
        return shared.getString(DEVICEPW, "12345678");
    }

    /**
     * 切换 局域网与联网环境
     *
     * @param env
     */
    public static void setEnv(int env) {
        SvrCommnuManager.shareInstance().environment = env;
        editor.putInt(ENV, env);
        editor.commit();
    }

    public static int getEnv() {
        return shared.getInt(ENV, 1);
    }

    /**
     * 记录的订阅的设备
     *
     * @return
     */
    public static void setLaserDevice(LaserDevice deivce) {
        if (deivce != null) {
            editor.putString(DEVICE_ID, deivce.deviceid);
            editor.putString(DEVICE_PASSWORD, deivce.equ_pwd);
            editor.commit();
        }
    }

    /**
     * 返回记录的订阅的设备
     *
     * @return
     */
    public static LaserDevice getSubcribeDevice() {
        String name = shared.getString(DEVICE_ID, "");
        String pw = shared.getString(DEVICE_PASSWORD, "");
        LaserDevice device = new LaserDevice(name, pw);
        return device;
    }

    /**
     * 返回记录的订阅的设备的id
     *
     * @return
     */
    public static String getSubcribeID() {
        String name = shared.getString(DEVICE_ID, "");
        return name;
    }

    /**
     * \
     *
     * @return
     */
    public static String getSubcribeFlag() {
        String name = shared.getString(DEVICE_ID, "");
        return "04" + HexUtil.cn2Hex(name);
    }

    /**
     * 专线数据的判断标准
     *
     * @return
     */
    public static String getRailwayFlag() {
        return "04" + shared.getString(NETMAC, "");
    }

    public static void setNetMacId(String macAddrss) {
        editor.putString(NETMAC, macAddrss);
        editor.commit();
    }

}
