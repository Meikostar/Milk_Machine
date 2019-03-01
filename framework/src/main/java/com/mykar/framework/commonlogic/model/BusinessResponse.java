package com.mykar.framework.commonlogic.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.mykar.framework.network.androidquery.callback.AjaxStatus;

public interface BusinessResponse
{    
	public abstract void OnMessageResponse(String url, JSONObject jo, AjaxStatus status) throws JSONException;
//	public abstract void OnMessageFailedResponse(String url, String errmsg,int errorCode);
}
