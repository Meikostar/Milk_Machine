package com.mykar.framework.commonlogic.model;

import org.json.JSONObject;

/**
 * Created by linquandong on 15/6/1.
 */
public interface BaseDelegate {

    //通用的网络返回，特殊的回调继承该BaseDelegate进行添加
    void onMessageResponseFail(String url, String msg, int code);
    void onMessageResponseSuccess(String url, JSONObject data);
}
