package com.mykar.framework.utils;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mykar.framework.ApplicationHolder;

import org.litepal.LitePalApplication;

/**
 * 复制一份代码，处理session过期的时候晴空用户信息
 */
public class LoginUtils {
    //String
    public static final String USEERINFO = "userInfo";

    private final static String UID = "uid";
    private final static String GID = "gid";
    // 记录自己的头像和昵称
    private final static String AVATAR = "avatar";
    private final static String NICKNAME = "nickname";

    private static SharedPreferences shared;
    private static Editor editor;

    static {
        shared = LitePalApplication.getContext().getSharedPreferences(USEERINFO, 0);
        editor = shared.edit();
    }

    public static boolean exitLogin()//退出登录
    {
        editor.putString(UID, "");
        editor.putString(AVATAR, "");
        editor.putString(NICKNAME, "");
        editor.putString(GID, "");
        editor.commit();
        //清空cookie信息
        return true;
    }


}
