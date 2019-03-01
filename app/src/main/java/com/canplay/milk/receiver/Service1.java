package com.canplay.milk.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 *
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service{

    @Override
    public void onCreate() {
        super.onCreate();
        // 获取系统的闹钟服务
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // 触发闹钟的时间（毫秒）
        long triggerTime = System.currentTimeMillis() + 1000000000;
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.setAction("com.canplay.alarm.lxm");
        PendingIntent op = PendingIntent.getBroadcast(this, 0, intent, 0);
        // 启动一次只会执行一次的闹钟
        am.set(AlarmManager.RTC, triggerTime, op);
        // 指定时间重复执行闹钟
        am.setRepeating(AlarmManager.RTC,triggerTime,2000,op);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
