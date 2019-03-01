package com.mykar.framework.commonlogic.protocol;

import org.json.JSONException;
import org.json.JSONObject;

public class STATUS  {


	public int ret;

	public String msg;

	public long timestamp;

	public static STATUS fromJson(JSONObject jsonObject) throws JSONException {

		STATUS localItem = new STATUS();
          if (null == jsonObject) {
              localItem.ret = -1;
              localItem.msg = "网络错误";
              return localItem;
          }else{
		localItem.ret = jsonObject.optInt("ret");
		localItem.msg = jsonObject.optString("msg");
		localItem.timestamp = jsonObject.optLong("timestamp");
          }
		return localItem;
	}

}
