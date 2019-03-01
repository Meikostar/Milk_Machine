package com.canplay.milk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;

import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.mykar.framework.activeandroid.util.Log;

import socket.bean.MilkConstant;

/**
 * Created by Administrator on 2018/6/28.
 */

public  class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "wifiReceiver";
    private long nowTime;
    private long pastTime;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            Log.i(TAG, "wifi信号强度变化");
        }
        //wifi连接上与否
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {



            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                Intent intents=new Intent("com.wifi.change");
                context.sendBroadcast(intents);
                RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.WIFISTATE,"1"));
                BaseApplication.state=0;
                BaseApplication.send=6;
                MilkConstant.HEAD="";
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                Intent intents=new Intent("com.wifi.change");
                context.sendBroadcast(intents);
                nowTime=System.currentTimeMillis();
                if(pastTime==0){
                    pastTime=nowTime;
                    RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.WIFISTATE,"2"));
                }else {
                    if((nowTime-pastTime)>10*1000){
                        pastTime=nowTime;
                        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.WIFISTATE,"2"));
                    }else {
                        pastTime=0;
                    }
                }

            }
        }
        //wifi打开与否
        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.WIFISTATE,"3"));
                BaseApplication.state=0;
                Log.i(TAG, "系统关闭wifi");
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                Log.i(TAG, "系统开启wifi");

            }
        }
    }
    public interface CallBackListener {
        void ReceiverListeners();
    }
    public CallBackListener listener;
    public void ReceiverCallBack( CallBackListener listener){
        this.listener=listener;
    }

}
