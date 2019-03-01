package com.canplay.milk.util;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by Administrator on 2018/4/3.
 */

public class gsonUtils {
//    Map<String,Map<String, Object>> pubArgs = new HashMap<>();
//    pubArgs.put("pub_args",pub_args);
//
//    Map<String,Map<String,Map<String, Object>>> args = new HashMap<>() ;
    //这个方法就是拼接参数的关键了，将传入的公参和私参拼接成json string
    public static String getGson(Map<String, Object> args) {

        Gson g = new Gson();
        String argsStr = g.toJson(args) ;

        return argsStr ;
    }
}
