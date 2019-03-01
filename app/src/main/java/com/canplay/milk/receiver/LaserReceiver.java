package com.canplay.milk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import de.greenrobot.event.EventBus;
import socket.laserevent.BaseLaserEvent;
import socket.laserevent.ConnectConstant;
import socket.laserevent.ConnectEvent;
import socket.laserevent.SateEvent;

/**
 * 自定义接收器
 */
public class LaserReceiver extends BroadcastReceiver {

    private BaseLaserEvent event = new BaseLaserEvent();
    private ConnectEvent eventConnect = new ConnectEvent();
    private SateEvent eventState = new SateEvent();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        int res = bundle.getInt("res");
        String msg = bundle.getString("msg");
        switch (res){
            case ConnectConstant.MSGRECEI_BUS://业务消息
                event.rescode = res;
                event.msg = msg;
                EventBus.getDefault().post(event);
                break;
            case ConnectConstant.MSGRECEI_STATE://广播消息
                eventState.rescode = res;
                eventState.msg = msg;
                EventBus.getDefault().post(eventState);
                break;

            default:
                //连接消息
                eventConnect.rescode = res;
                eventConnect.msg = msg;
                EventBus.getDefault().post(eventConnect);
                break;
        }
    }
}
