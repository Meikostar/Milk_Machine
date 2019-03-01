package com.mykar.framework.commonlogic.model;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class LogicService {
    private static final String TAG = "LogicService";

    private static HashMap<String,Object> singleInstanceMap = new HashMap<String, Object>();
    private LogicService()
    {

    }

    public static <T extends Object> T getSingletonManagerForClass(Class<T> cls)
    {
        T mgr = null;
        String className = cls.getSimpleName();
        if(singleInstanceMap.containsKey(className))
        {
            mgr = (T) singleInstanceMap.get(className);
        }
        if(mgr==null)
        {
            try
            {
                //调用私有的构造方法
                Constructor constructor=cls.getDeclaredConstructor();
                constructor.setAccessible(true);
                mgr =  (T) constructor.newInstance();
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getMessage());
            }
            if (mgr != null)
            {
                singleInstanceMap.put(cls.getSimpleName(), mgr);
                Log.d(TAG, String.format("findByLogicManagerClass result: %s", mgr.getClass().getName()));
            }
            else
            {
                Log.e(TAG, String.format("fail to find logic manager %s", cls.getSimpleName()));
            }
        }
        return mgr;
    }
}