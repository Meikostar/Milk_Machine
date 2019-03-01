package com.mykar.framework.commonlogic.model;

import com.google.gson.Gson;
import com.mykar.framework.ApplicationHolder;
import com.mykar.framework.KLog.KLog;
import com.mykar.framework.commonlogic.protocol.STATUS;
import com.mykar.framework.network.androidquery.callback.AjaxCallback;
import com.mykar.framework.utils.CookieUtils;
import com.mykar.framework.utils.LoginUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePalApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linquandong on 15/6/1.
 */
public class BaseSingleModel {
    protected BeeQuery aq;
    protected STATUS responStatus;
    protected JSONObject dataJson;
    protected Gson mGson;
    private int index;
    private boolean hasNext;

    protected BaseSingleModel() {
        aq = new BeeQuery(LitePalApplication.getContext());
        mGson = new Gson();
    }

    public void callback(String url, JSONObject jo, BaseDelegate delegate) {
        try {
            responStatus = STATUS.fromJson(jo);
            if (responStatus.ret != 0) {
                dataJson = null;
                checkLoginState(responStatus.ret);
                delegate.onMessageResponseFail(url, responStatus.msg, responStatus.ret);
            } else {
                delegate.onMessageResponseSuccess(url, jo);
                dataJson = jo.optJSONObject("data");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        printJson(url, jo);
    }

    private void printJson(String url, JSONObject jo) {
        AjaxCallback callback = aq.getCallback();
        String paramString = "";
        if (callback != null) {
            HashMap<String, Object> params = (HashMap<String, Object>) callback.getParams();
            paramString = getParamString(params);
        }
        //输出json日志
        KLog.i("op_json", url + "========>");
        KLog.json("op_json==>param", paramString);
        if (jo == null) {
            KLog.i("op_json", "null");
        } else {
            KLog.i("op_json", jo.toString());
        }
    }

    private String getParamString(HashMap<String, Object> params) {
        if (params == null) {
            return "";
        }
        JSONObject json = new JSONObject();
        try {
            for (Map.Entry entry : params.entrySet()) {
                json.put(entry.getKey().toString(), entry.getValue());
            }
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    //if登录不成功则情况SharePreferent里的userInfo
    public void checkLoginState(int resultCode) {
        //session过期 与 ErrorCodeConst.NOLOGIN 的错误码一一对应
        if (resultCode == 101110) {
            CookieUtils.clearCookie();
            LoginUtils.exitLogin();
        }
    }    //加载更多

    public void setIndex(JSONObject dataJson) {
        index = dataJson.optInt("page");
        int sum = dataJson.optInt("count");
        hasNext = sum > index ? true : false;
    }

    public void isLoadMore(boolean isLoadMore) {
        index = isLoadMore ? index : 0;
    }

    //判断是否是加载更多
    public boolean isLoadMore() {
        return index != 0;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    /**
     * 用于分页的页数：param("index",getIndex())
     * @return
     */
    public int getIndex() {
        return index;
    }


    public void cancelRquest() {
        aq.ajaxCancel();
    }
}
