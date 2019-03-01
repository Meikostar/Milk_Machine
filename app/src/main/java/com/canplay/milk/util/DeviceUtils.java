package com.canplay.milk.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by xzh on 16/7/6.
 */
public class DeviceUtils {
    private TelephonyManager tm;
    private Context mContext;
    public static final String TAG = "device_info";

    public DeviceUtils(Context context) {
        mContext = context;
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }


    /**
     * 获取CPU名字
     */
    public static String getCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取设备版本号
     */
    public static String getDeviceVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取设备序列号
     */
    public static String getDeviceSerial() {
        return android.os.Build.SERIAL;
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    public long getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return blockSize * totalBlocks;
//        return Formatter.formatFileSize(mContext, blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return blockSize * availableBlocks;
//        return Formatter.formatFileSize(mContext, blockSize * availableBlocks);
    }

    public long getSDUsedSize(){
        return getSDTotalSize() - getSDAvailableSize();
    }

    /**
     * 获得机身内存总大小
     * @return
     */
    public long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return blockSize * totalBlocks;
//        return Formatter.formatFileSize(mContext, blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     *
     * @return
     */
    public long getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return blockSize * availableBlocks;
//        return Formatter.formatFileSize(mContext, blockSize * availableBlocks);
    }

    public long getRomUsedSize(){
        return getRomTotalSize() - getRomAvailableSize();
    }

    /**
     * 获取外置存储卡的根路径，如果没有外置存储卡，则返回null
     * 参数 is_removable为false时得到的是内置sd卡路径，为true则为外置sd卡路径。
     */
    public static String getStoragePath(Context mContext, boolean is_removale) {
        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

//    /**
//     * 通过packagename启动应用
//     * @param context
//     * @param packagename
//     * */
//    public static void startAPPFromPackageName(Context context,String packagename){
//        Intent intent=isexit(context,packagename);
//        if(intent==null){
//            Log.i(TAG,"APP not found!");
//            return;
//        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//    }
    public static void openApp(Context context, String packageName) {

        try {
            //微视听直接打开动画
            if(weishiopen(context,packageName)){
             return;
            }

            PackageInfo pi = null;
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(packageName, 0);

            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(pi.packageName);

            List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);

            ResolveInfo ri = apps.iterator().next();
            if (ri != null ) {
                String className = ri.activityInfo.name;

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);

                ComponentName cn = new ComponentName(packageName, className);

                intent.setComponent(cn);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static boolean weishiopen(Context context, String packageName) {
        if(packageName.equals("net.myvst.v2")){
            Intent intent = new Intent();
            intent.setAction("android.intent.action.vst.children");
            context.startActivity(intent);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 通过packagename判断应用是否安装
     * @param context
     *
     * @return 跳转的应用主activity Intent
     * */

    public static Intent isexit(Context context, String pk_name){
        PackageManager packageManager = context.getPackageManager();
        Intent it= packageManager.getLaunchIntentForPackage(pk_name);
        return it;
    }

}
