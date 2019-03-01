package com.canplay.milk.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.canplay.milk.mvp.activity.AlarmActivity;


/**
 * Created by Administrator on 2018/4/13.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 获得提示信息
        String msg = intent.getStringExtra("msg");
        if (intent.getAction()!=null&&intent.getAction().equals("com.canplay.alarm.lxm")) {//自定义的action
            Intent freshIntent = new Intent();
            freshIntent.setAction("com.android.music.musicservicecommand.pause");
            freshIntent.putExtra("command", "pause");
            context.sendBroadcast(freshIntent);
            Intent i = new Intent();  //自定义打开的界面
        i.putExtra("msg",msg);
        i.setClass(context, AlarmActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);}
    }


}
