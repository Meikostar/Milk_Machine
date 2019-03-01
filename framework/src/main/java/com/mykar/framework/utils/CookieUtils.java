package com.mykar.framework.utils;

import android.content.SharedPreferences;

import com.mykar.framework.ApplicationHolder;
import com.mykar.framework.network.androidquery.callback.AjaxStatus;

import org.apache.http.Header;
import org.litepal.LitePalApplication;

import java.util.Calendar;

/**
 * Created by linquandong on 15/7/7.
 */
public class CookieUtils {

    private static SharedPreferences shared;
    private static SharedPreferences.Editor editor;
    private static final String COOKIEFLAGS = "cookie";
    private static final String COOKIE_STARTTIME = "cookie_startime";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final long COOKIE_LIFE = 24*7*5;//有效时间5个星期（单位为小时）

    static {
        shared = LitePalApplication.getContext().getSharedPreferences(COOKIEFLAGS, 0);
        editor = shared.edit();
    }

    public static void getCookies(AjaxStatus status) {
        for (Header header : status.getHeaders()) {
            if (header.getName().equalsIgnoreCase(SET_COOKIE)) {
                editor.putString(header.getName(), header.getValue());
                editor.putLong(COOKIE_STARTTIME, getPresentime());
            }
        }
        editor.commit();
    }

    private static long getPresentime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime().getTime();
    }

    public static String makeCookie() {
        if (shared == null || shared.getAll().size() == 0) {
            return null;
        }
        return shared.getString(SET_COOKIE, null);

    }

   /***
    * cookie是否过了有效时间
    * 如果过期则清空cookie
    *
    * */
    public  static boolean isOutTime() {
        long cookie_startime = shared.getLong(COOKIE_STARTTIME,0);
        long duration = getPresentime() - cookie_startime;
        long hours = duration/(1000*60*60);
        if(hours>=COOKIE_LIFE)
        {
            editor.putString(SET_COOKIE,null);
            editor.commit();
            return  true;
        }
        return false;
    }

    //清空cookie
    public static void clearCookie() {
        editor.putString(SET_COOKIE,null);
        editor.commit();
    }
}
