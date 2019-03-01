package com.mykar.framework.commonlogic.model;

import android.content.Context;

import com.mykar.framework.network.androidquery.AQuery;
import com.mykar.framework.network.androidquery.callback.AjaxCallback;

import java.lang.ref.WeakReference;
import java.util.Map;

public class BeeQuery<T> extends AQuery {
    public BeeQuery(Context context) {
        super(context);
    }

    public static final int ENVIRONMENT_PRODUCTION = 1;
    public static final int ENVIROMENT_DEVELOPMENT = 2;
    public static final int ENVIROMENT_MOCKSERVER = 3;

    public static int environment() {
        return ENVIRONMENT_PRODUCTION;
    }

//    public static String staticurl = "http://www.5iicp.com/gscms-TP/api/"; //开放环境
    public static String staticurl = "http://laser.yunchedian.com/index.php/app/";//生产环境

    public final static String DOWNLOAD = staticurl + "news/down";//网页分享
    public final static String SHARE_LOGO = "http://yyrtv.mykar.com/108-108.png";//分享的Logo

    private WeakReference<AjaxCallback> callback;

    public static String serviceUrl() {
        if (ENVIRONMENT_PRODUCTION == BeeQuery.environment()) {
            return staticurl;
        } else {
            return staticurl;
        }
    }

    public AjaxCallback getCallback() {
        if (callback != null) {
            return callback.get();
        } else {
            return null;
        }
    }

    public <K> AQuery ajax(AjaxCallback<K> callback) {
        this.callback = new WeakReference<AjaxCallback>(callback);

        if (BeeQuery.environment() == BeeQuery.ENVIROMENT_MOCKSERVER) {
            MockServer.ajax(callback);
            return null;
        } else {
            String url = callback.getUrl();
            String absoluteUrl = getAbsoluteUrl(url);

            callback.url(absoluteUrl);
        }

        if (BeeQuery.environment() == BeeQuery.ENVIROMENT_DEVELOPMENT) {
            DebugMessageModel.addMessage((BeeCallback) callback);
        }
        return (BeeQuery) super.ajax(callback);
    }

    public <K> AQuery ajaxAbsolute(AjaxCallback<K> callback) {

        return (BeeQuery) super.ajax(callback);
    }

    public <K> AQuery ajax(String url, Map<String, ?> params, Class<K> type,
                           BeeCallback<K> callback) {

        callback.type(type).url(url).params(params);

        if (BeeQuery.environment() == BeeQuery.ENVIROMENT_MOCKSERVER) {
            MockServer.ajax(callback);
            return null;
        } else {
            String absoluteUrl = getAbsoluteUrl(url);
            callback.url(absoluteUrl);
        }
        return ajax(callback);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BeeQuery.serviceUrl() + relativeUrl;
    }
}