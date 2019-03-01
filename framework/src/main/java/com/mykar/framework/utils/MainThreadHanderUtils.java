package com.mykar.framework.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by linquandong on 16/8/8.
 */
public class MainThreadHanderUtils {

    public static Handler getMainHandler(){
        return new Handler(Looper.getMainLooper());
    }

    public static void postOnMainThread(Runnable task){
        getMainHandler().post(task);
    }

    public static void postOnMainThreadDelay(Runnable task,long delayMillis){
        getMainHandler().postDelayed(task,delayMillis);
    }
}
