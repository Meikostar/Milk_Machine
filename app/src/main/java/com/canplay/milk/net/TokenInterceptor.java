package com.canplay.milk.net;

import android.os.Handler;


import com.canplay.milk.util.SpUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/***
 * 功能描述:
 * 作者:chenwei
 * 时间:2016/12/23
 * 版本:
 ***/
public class TokenInterceptor implements Interceptor {
    private static Handler handler = new Handler(){};

    @Override
    public Response intercept(Chain chain) throws IOException {


        Request request = chain.request().newBuilder()

                .build();
        return chain.proceed(request);

    }


}
