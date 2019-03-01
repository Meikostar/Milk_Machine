package com.canplay.milk.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.canplay.milk.base.ApplicationConfig;
import com.canplay.milk.bean.AlarmClock;
import com.canplay.milk.bean.BASE;
import com.canplay.milk.bean.SetMilk;
import com.canplay.milk.bean.USER;
import com.canplay.milk.bean.Wather;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SpUtil {
    public static String PREFERENCE_NAME = "repast_app_sp";

    private static SpUtil instance;
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String COMPANYID = "company_id";
    public static final String PROVINCE = "province";
    public static final String WEIGHT = "weight";
    public static final String BIRTHDAY = "birthday";
    public static final String CONTURY = "contury";//国家
    public static final String TOKEN = "access_token";//国家
    public static final String TOKEN_TYPE = "token_type";//国家
    public static final String USERID = "userId";//国家
    public static final String USERNAME = "userName";//国家
    public static final String FATHERTNAME = "fatherName";//国家
    public static final String PHONE = "phone";//国家
    public static final String SEX = "sex";//国家
    public static final String AVATOR = "imgResourceKey";//国家
    public static final String MOTHERNAME = "motherName";//国家
    public static final String ML = "mls";//国家
    public static final String WD = "wds";//国家
    public static final String ND = "nds";//国家
    public static final String ONPENSTATE = "remindStatus";//国家
    public static final String REMINDSTATE = "remindSta";//国家
    public static final String NEXT_TIME = "nextTime";//国家
    public static final String PRE_TIME = "previousTime";//国家
    public static final String BASE_ID = "baseId";//国家
    public static final String NAME_BASE = "name_base";//国家
    public static final String FREQUENCY = "frequency";//国家
    public static final String VALID_STATUS = "validStatus";//国家
    public static final String MILK_ID = "milkInfoId";//国家
    public static final String WD_WATHER = "wd_wather";//国家
    public static final String ML_WATHER = "ml_wather";//国家
    public static final String IP = "ip_config";//国家
    public static final String WEIGHT_D = "weight_d";//国家
    public static final String WEIGHT_W= "weight_w";//国家
    public static final String WIFINAME = "wifiNames";//国家
    public static final String PER = "milk_per";//基数
    private static SharedPreferences settings;
    public static String USER_ID = "merchantId";

    private SpUtil() {
        settings = ApplicationConfig.context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }


    private static List<AlarmClock> list;

    public boolean putUser(USER location) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TOKEN, location.token + "");
        editor.putString(BIRTHDAY, location.birthday + "");
        editor.putString(USERID, location.userId);
        editor.putString(USERNAME, location.name);
        editor.putString(AVATOR, location.imgResourceKey);
        editor.putString(MOTHERNAME, location.motherName);
        editor.putString(FATHERTNAME, location.fatherName);
        editor.putString(PHONE, location.mobile);
        editor.putString(SEX, location.sex);
        editor.putString(WEIGHT, location.weight);
        return editor.commit();
    }


    public boolean putMilk(SetMilk location) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(ML, location.waterQuantity + "");
        editor.putString(WD, location.waterTemperature + "");
        editor.putString(ND, location.consistence);
        editor.putInt(ONPENSTATE, location.vaccineRemindStatus);
        editor.putInt(REMINDSTATE, location.remindStatus);
        editor.putString(MILK_ID, location.milkInfoId);

        return editor.commit();
    }

    public boolean putBASE(BASE location) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(NEXT_TIME, location.nextTime);
        editor.putLong(PRE_TIME, location.previousTime);
        if (location.nextVaccine != null) {
            editor.putString(BASE_ID, location.userVaccineId);
            editor.putString(NAME_BASE, location.nextVaccine.name);
            editor.putString(FREQUENCY, location.nextVaccine.frequency);
            editor.putInt(VALID_STATUS, location.nextVaccine.validStatus);
        }


        return editor.commit();
    }

    public boolean putWahter(Wather wather) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(WD_WATHER, wather.wd);
        editor.putFloat(ML_WATHER, wather.ml);


        return editor.commit();
    }

    public boolean putIp(String ip) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(IP, ip);
        return editor.commit();
    }

    public Wather getWahter() {
        Wather wather = new Wather();
        wather.wd = settings.getInt(WD_WATHER, 0);
        wather.ml = settings.getFloat(ML_WATHER, 0);

        return wather;
    }

    /**
     * 返回单例对象
     *
     * @return SpUtil
     */
    public static SpUtil getInstance() {
        if (instance == null) {
            synchronized (SpUtil.class) {
                if (instance == null) {
                    instance = new SpUtil();
                    list = new ArrayList<>();
                }
            }
        }
        return instance;
    }

    public List<AlarmClock> getAllAlarm() {
        list.clear();
        String time = settings.getString("time", "");
        if (TextUtil.isNotEmpty(time)) {
            String[] split = time.split(",");
            for (int i = 0; i < split.length; i++) {
                String data = settings.getString(split[i], "");
                if (TextUtil.isNotEmpty(data)) {
                    Gson gson = new Gson();
                    AlarmClock alarmClock = gson.fromJson(data, AlarmClock.class); //将json字符串转换成 AlarmClock对象
                    list.add(alarmClock);
                }
            }
        }
        return list;
    }

    public boolean clearData() {
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        return editor.commit();
    }

    public USER getUsers() {
        USER user = new USER();
        user.token = settings.getString(TOKEN, "");
        user.birthday = settings.getString(BIRTHDAY, "");
        user.userId = settings.getString(USERID, "");

        user.name = settings.getString(USERNAME, "");
        user.motherName = settings.getString(MOTHERNAME, "");
        user.fatherName = settings.getString(FATHERTNAME, "");
        user.mobile = settings.getString(PHONE, "");
        user.imgResourceKey = settings.getString(AVATOR, "");
        user.sex = settings.getString(SEX, "");
        user.weight = settings.getString(WEIGHT, "");
        return user;
    }

    public SetMilk getMilk() {

        SetMilk user = new SetMilk();
        user.waterTemperature = settings.getString(WD, "");
        user.waterQuantity = settings.getString(ML, "");
        user.consistence = settings.getString(ND, "");
        user.remindStatus = settings.getInt(ONPENSTATE, 0);
        user.milkInfoId = settings.getString(MILK_ID, "");

        return user;
    }
    public Integer getAllState() {

        int ips = settings.getInt(REMINDSTATE, 0);
        return ips;
    }
    public String getIp() {

        String ip = settings.getString(IP, "");
        return ip;
    }
    public String getWeight() {

        String ip = settings.getString(WEIGHT_D, "");
        return ip;
    }
    public String  getMilkWeight( int ml) {
        double a=Double.valueOf(getMilkPer());

        return (int)(ml*a)+"";
    }
//    public String getMilkWeight() {
//
//        String ip = settings.getString(WEIGHT_W, "");
//        return ip;
//    }
    public String getMilkPer() {

        String ip = settings.getString(PER, "");
        if(TextUtil.isEmpty(ip)){
            return "0.17";
        }else {
            return ip;
        }

    }
    public BASE getBASE() {

        BASE user = new BASE();
        user.previousTime = settings.getLong(PRE_TIME, 0);
        user.nextTime = settings.getLong(NEXT_TIME, 0);
        user.id = settings.getString(BASE_ID, "");
        user.name = settings.getString(NAME_BASE, "");
        user.frequency = settings.getString(FREQUENCY, "");
        user.validStatus = settings.getInt(VALID_STATUS, 0);

        return user;
    }

    public String getUserId() {
        return settings.getString(USERID, "");
    }

    public long getNextTime() {
        return settings.getLong(NEXT_TIME, 0);
    }

    public String getToken() {
        return settings.getString(TOKEN, "");
    }

    public String getTokenType() {
        return settings.getString(TOKEN_TYPE, "");
    }

    public String getUser() {
        return settings.getString(USERNAME, "");
    }

    public String getAvator() {
        return settings.getString(AVATOR, "");
    }

    /**
     * put string preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public boolean putString(String key, String value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * get string preferences
     *
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws ClassCastException if there is a preference with this
     * name that is not a string
     * @see #getString(String, String)
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * get string preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a string
     */
    public String getString(String key, String defaultValue) {
        return settings.getString(key, defaultValue);
    }

    /**
     * put int preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public boolean putInt(String key, int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * get int preferences
     *
     * @param
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a int
     * @see #getInt(String, int)
     */
    public int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     * get int preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a int
     */
    public int getInt(String key, int defaultValue) {
        return settings.getInt(key, defaultValue);
    }

    /**
     * put long preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public boolean putLong(String key, long value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * get long preferences
     *
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a long
     * @see #getLong(String, long)
     */
    public long getLong(String key) {
        return getLong(key, -1);
    }

    /**
     * get long preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a long
     */
    public long getLong(String key, long defaultValue) {
        return settings.getLong(key, defaultValue);
    }

    /**
     * put float preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public boolean putFloat(String key, float value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * get float preferences
     *
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a float
     * @see #getFloat(String, float)
     */
    public float getFloat(String key) {
        return getFloat(key, -1);
    }

    /**
     * get float preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a float
     */
    public float getFloat(String key, float defaultValue) {
        return settings.getFloat(key, defaultValue);
    }

    /**
     * put boolean preferences
     *
     * @param key   The name of the preference to modify
     * @param value The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public boolean putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * get boolean preferences, default is false
     *
     * @param key The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws ClassCastException if there is a preference with this
     * name that is not a boolean
     * @see #getBoolean(String, boolean)
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * get boolean preferences
     *
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a boolean
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return settings.getBoolean(key, defaultValue);
    }
}

