package com.canplay.milk.util;

import android.util.Log;
import android.widget.Toast;

import com.canplay.milk.base.BaseApplication;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;


/**
 * 管理Log 的类.
 */
public class LogUtils {
    public static void showToast(final String msg){
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (msg!=null){
                    subscriber.onNext(msg);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onNext(String s) {
                        Toast.makeText(BaseApplication.getInstance(),s,Toast.LENGTH_LONG).show();
                    }
                });
    }
    private static final String TAG = "ChaoJiPi";
    private static boolean DEBUG=true;
    public static void d(String TAG, String content){
        if(DEBUG){
            if (StringUtil.isNotEmpty(content)){
                Log.d(TAG, content);
            }

//            LogToFile.init(YunShlApplication.mYunShlApplication);
//            LogToFile.d(TAG,content);
        }
    }
    public static void e(String TAG, String content){
        if(DEBUG){
            if (StringUtil.isNotEmpty(content)){
                Log.e(TAG, content);
            }

//            LogToFile.init(YunShlApplication.mYunShlApplication);
//            LogToFile.e(TAG,content);
        }
    }

    public static void i(String TAG, String content){
        if(DEBUG){
            if (StringUtil.isNotEmpty(content)){
                Log.i(TAG, content);
            }

//            LogToFile.init(YunShlApplication.mYunShlApplication);
//            LogToFile.e(TAG,content);
        }
    }

    public static void i(String content){
        if(DEBUG){
            if (StringUtil.isNotEmpty(content)){
                Log.i(TAG, content);
            }

//            LogToFile.init(YunShlApplication.mYunShlApplication);
//            LogToFile.e(TAG,content);
        }
    }

    public static void w(String TAG, String content){
        if(DEBUG){
            if (StringUtil.isNotEmpty(content)){
                Log.w(TAG, content);
            }
        }
    }
    public static void w(String content){
        if(DEBUG){
            if (StringUtil.isNotEmpty(content)){
                Log.w(TAG, content);
            }

//            LogToFile.init(YunShlApplication.mYunShlApplication);
//            LogToFile.e(TAG,content);
        }
    }


//    public static void writeLogToFile(final String TAG, final String content){
//        LogToFile.init(YunShlApplication.mYunShlApplication);
//        LogToFile.d(TAG,content);
//    }

}
