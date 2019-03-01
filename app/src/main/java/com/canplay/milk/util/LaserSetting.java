package com.canplay.milk.util;

import android.content.SharedPreferences;


import com.canplay.medical.R;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.bean.SETTING;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linquandong on 17/1/9.
 */
public class LaserSetting {
    public static final String LASERSETTING = "lasersettings";
    private static LaserSetting intance;

    private static SharedPreferences shared;
    private static SharedPreferences.Editor editor;

    static {
        shared = BaseApplication.getInstance().getSharedPreferences(LASERSETTING, 0);
        editor = shared.edit();
    }

    private List<SETTING> settings = new ArrayList<>();;

    public List<SETTING> getSettings() {
        updateSettings();
        return settings;
    }

    private LaserSetting() {
        updateSettings();
    }

    public void updateSettings(){
//        settings = new Select().from(SETTING.class).execute();
//        if (settings.size() == 10) {
//
//        } else {
            settings.clear();
            initSetting(1, "envtemper", FR.str(R.string.envtemper),1);
            initSetting(2, "humidity", FR.str(R.string.humidity),1);
            initSetting(3, "opticaltemper1", FR.str(R.string.opticaltemper1),1);
            initSetting(4, "opticaltemper2", FR.str(R.string.opticaltemper2),1);
            initSetting(5, "opticaltemper3", FR.str(R.string.opticaltemper3),1);
            initSetting(6, "vapourpress", FR.str(R.string.vapourpress),0.1f);
            initSetting(7, "waterspeed", FR.str(R.string.waterspeed),0.1f);
            initSetting(8, "laserpowerset", FR.str(R.string.laserpowerset),0.01f);
            initSetting(9, "laserpowerfk", FR.str(R.string.laserpowerfk),0.01f);
            initSetting(0, "watertemper", FR.str(R.string.watertemper),1);
//        }
    }

    public void initSetting(int positon, String fiedName, String fiedDesc, float scale) {
        SETTING setting = new SETTING();
        setting.postion = positon;
        setting.fieldName = fiedName;
        setting.fiedDesc = fiedDesc;
        setting.scale = scale;
        settings.add(setting);
    }

    public static LaserSetting shareInstance() {

        if (intance == null) {
            intance = new LaserSetting();
        }
        return intance;
    }

    public String getDataSetDesc(int postion) {
        if(settings.size() > postion){
            SETTING setting = settings.get(postion);
            return setting.fiedDesc;
        }
        return "";
    }

    public SETTING getSetting(int postion) {
         return settings.get(postion);
    }
}
