package com.canplay.milk.util;

import android.content.res.Resources;

import com.canplay.milk.base.BaseApplication;


/**
 * Created by linquandong on 15/6/1.
 */
public class FR {

   static Resources resource = (Resources) BaseApplication.getInstance().getResources();

    public static String str(int id){
        return resource.getString(id);
    }

    public static int getColor(int colorId) {
        return resource.getColor(colorId);
    }
}
