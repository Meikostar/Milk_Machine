package com.canplay.milk.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.mykar.framework.network.androidquery.AQuery;
import com.mykar.framework.network.androidquery.callback.AjaxCallback;
import com.mykar.framework.network.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by linquandong on 16/11/8.
 */
public class UpLoadModel implements Dialog_Update.UpdateListener {
    public static final int UPDATE = 1;
    public static final int CANCEL = 0;
    public static final int NONEED = 2;

    public static final String url = "http://mykarapp.mykar.com/api/index/update";
    private static UpLoadModel upLoadModel;
    public String packname = "";
    public int versioncode;
    public String channel = "";

    public Dialog_Update dialogUpdate;

    private UpLoadModel() {

    }

    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getChannl(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName =
                (String) packageManager.getApplicationLabel(applicationInfo);
        return applicationName;
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    public static UpLoadModel sharainstance() {
        if (upLoadModel == null) {
            upLoadModel = new UpLoadModel();
        }
        return upLoadModel;
    }


    @Override
    public void updateChoose(Context context, int i) {

    }
}
