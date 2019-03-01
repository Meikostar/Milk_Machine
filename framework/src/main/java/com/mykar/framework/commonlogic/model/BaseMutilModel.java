package com.mykar.framework.commonlogic.model;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.mykar.framework.ApplicationHolder;
import com.mykar.framework.commonlogic.protocol.STATUS;
import com.mykar.framework.network.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePalApplication;

import java.util.ArrayList;

public  class BaseMutilModel {

    protected BeeQuery aq;
    protected ArrayList<BaseDelegate> baseDelegateList = new ArrayList<BaseDelegate>();
    protected Context mContext;
    protected STATUS responStatus;
    protected Gson mGson;
    protected JSONObject dataJson;

    public BaseMutilModel() {
        aq = new BeeQuery(LitePalApplication.getContext());
        mGson = new Gson();
    }

    protected void saveCache() {
        return;
    }

    protected void cleanCache() {
        return;
    }

    public void addDelegate(BaseDelegate listener) {
        if (!baseDelegateList.contains(listener)) {
            baseDelegateList.add(listener);
        }
    }

    public void removeDelegate(BaseDelegate listener) {
        if(baseDelegateList.contains(listener)){
            baseDelegateList.remove(listener);
        }
    }

    /**
     * 返会boolean类型，判断网络数据是否是可以解析的
     */
    public void callback(String url, JSONObject jo, AjaxStatus status) {
        try {

            responStatus = STATUS.fromJson(jo);
            if(responStatus.ret != 0){
                for (BaseDelegate iterable_element : baseDelegateList) {
                    onFail(url,jo,iterable_element);
                }
            }else{
                dataJson = jo.optJSONObject("data");
                for (BaseDelegate iterable_element : baseDelegateList) {
                    onSuccess(url,jo,iterable_element);
                }
            }
            //输出json日志
            if(jo == null)
            {
                Log.i("op_json", url+"=====>"+"null");
            }else{
                Log.i("op_json", url+"=====>"+jo.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //子类必须重新的方法,如果需要返回通用的成功和回调则在子类中super下面的两个方法即可
    public  void onSuccess(String url, JSONObject jo,BaseDelegate delegate){
        delegate.onMessageResponseSuccess(url,jo);
    }
    public  void onFail(String url,JSONObject jo,BaseDelegate delegate){
        delegate.onMessageResponseFail(url,responStatus.msg,responStatus.ret);
    }

}
