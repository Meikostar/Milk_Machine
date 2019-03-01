package com.canplay.milk.util;


import android.widget.Toast;


import com.canplay.milk.base.BaseApplication;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/***
 * 功能描述:
 * 作者:qiujialiu
 * 时间:2016/12/19
 * 版本:
 * ===================
 ***/
public class ToastUtil {

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
                        Toast.makeText(BaseApplication.getInstance(),s, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public static void showToastOnUI(String msg){
        Toast.makeText(BaseApplication.getInstance(),msg, Toast.LENGTH_SHORT).show();
    }

    public static void showIOExceptionMessage(final Exception e){
        if (e instanceof ConnectException || e instanceof ConnectTimeoutException
                || e instanceof SocketTimeoutException || e instanceof SocketException ||
                e instanceof UnknownHostException){
            showToast(""+e.getMessage());
        }
    }

}
