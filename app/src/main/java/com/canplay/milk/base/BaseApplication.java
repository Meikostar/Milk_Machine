package com.canplay.milk.base;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.multidex.MultiDex;
import android.view.View;


import com.canplay.medical.R;
import com.canplay.milk.base.manager.AppManager;
import com.canplay.milk.bean.AlarmClock;
import com.canplay.milk.receiver.Receiver1;
import com.canplay.milk.receiver.Receiver2;
import com.canplay.milk.receiver.Service1;
import com.canplay.milk.receiver.Service2;
import com.canplay.milk.util.ExceptionHandler;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.ThirdShareManager;
import com.google.zxing.client.android.decode.WeacConstants;
import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;


import java.util.HashMap;
import java.util.Map;

import io.valuesfeng.picker.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import io.valuesfeng.picker.universalimageloader.core.ImageLoader;
import io.valuesfeng.picker.universalimageloader.core.ImageLoaderConfiguration;
import io.valuesfeng.picker.universalimageloader.core.assist.QueueProcessingType;
import socket.bean.MilkConstant;
import socket.util.HexUtil;
import socket.util.WifiUtils;
import udpsocket.core.TcpClientManager;
import udpsocket.core.UDPSocketBroadCast;



/**
 * App基类
 * Created by peter on 2016/9/11.
 */

public class BaseApplication extends DaemonApplication {
    //全局单例
    AppComponent mAppComponent;
    public static  BaseApplication cplayApplication;
    public static String url="http://oss3m8u82.bkt.clouddn.com/";
    public static String ip="";
    public static String port="5000";
    public static String day="20";
    public static int state=0;//0断开，1链接
    public static  long phoneState=0;//1通话中，2 响铃 0，挂断和空闲
    public static String waterQuantity;
    public static int send=0;
    public static int status=0;
    public static int isReself=0;
    public static boolean bind=false;
    public static boolean stop=false;
    public static Map<String,String> map=new HashMap<>();
    public static BaseApplication getInstance() {
        if (cplayApplication == null) {
            cplayApplication = new BaseApplication();
        }
        return  cplayApplication;
    }

    public  Vibrator mVibrator;
    @Override
    public void onCreate(){
        // TODO Auto-generated method stub
        super.onCreate();
        //生成全局单例
        cplayApplication = this;
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
        mAppComponent.inject(this);
        ApplicationConfig.setAppInfo(this);
        unSppuortSystemFont();
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        ThirdShareManager.getInstance().init(this);
        TcpClientManager.getInstance();
        //全局异常处理
        initImageLoader(this);
        new ExceptionHandler().init(this);
        mAlarmClock = new AlarmClock();
        // 闹钟默认开启
        mAlarmClock.setOnOff(true);
        // 保存设置的音量
        mAlarmClock.setVolume(15);

        // 初始化闹钟实例的小时
        mAlarmClock.setHour(9);
        // 初始化闹钟实例的分钟
        mAlarmClock.setMinute(30);
        // 默认小睡
        mAlarmClock.setNap(true);
        // 小睡间隔10分钟
        mAlarmClock.setNapInterval(5);
        // 小睡3次

        // 取得铃声选择配置信息
        SharedPreferences share = getSharedPreferences(
                WeacConstants.EXTRA_WEAC_SHARE, Activity.MODE_PRIVATE);
        String ringName = share.getString(WeacConstants.RING_NAME,
                getString(R.string.default_ring));
        String ringUrl = share.getString(WeacConstants.RING_URL,
                WeacConstants.DEFAULT_RING_URL);

        // 初始化闹钟实例的铃声名
        mAlarmClock.setRingName(ringName);
        // 初始化闹钟实例的铃声播放地址
        mAlarmClock.setRingUrl(ringUrl);
        mAlarmClock.setRepeat("每天");
        // 响铃周期
        mAlarmClock.setWeeks("2,3,4,5,6,7,1");
       if(WifiUtils.shareInstance().isUseable()){

               final UDPSocketBroadCast broadCast=new UDPSocketBroadCast();
               broadCast.startUDP(new UDPSocketBroadCast.UDPDataCallBack() {
                   @Override
                   public void mCallback(String str) {
                       ip= getHex(str.substring(22,24))+"."+getHex(str.substring(24,26))+"."+getHex(str.substring(26,28))+"."+getHex(str.substring(28,30));

                       if(TextUtil.isNotEmpty(ip)){

                           MilkConstant.HEAD=str.substring(0,20);
                           MilkConstant.HD=str.substring(0,20);
                           MilkConstant.EQUIPT=str.substring(8,20);
                           RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.CONNECT,""));
                           SpUtil.getInstance().putString(SpUtil.WIFINAME,WifiUtils.shareInstance().getWifiName());
                           TcpClientManager.getInstance().startTcpClient();
                       }

                   }
               });


       }
        countDownTimer = new CountDownTimer(1000*3600*48, 20000) {
            @Override
            public void onTick(long millisUntilFinished) {
             if(TextUtil.isEmpty(MilkConstant.HEAD)){
                 if(WifiUtils.shareInstance().isUseable()){

                     final UDPSocketBroadCast broadCast=new UDPSocketBroadCast();
                     broadCast.startUDP(new UDPSocketBroadCast.UDPDataCallBack() {
                         @Override
                         public void mCallback(String str) {
                             ip= getHex(str.substring(22,24))+"."+getHex(str.substring(24,26))+"."+getHex(str.substring(26,28))+"."+getHex(str.substring(28,30));

                             if(TextUtil.isNotEmpty(ip)){

                                 MilkConstant.HEAD=str.substring(0,20);
                                 MilkConstant.HD=str.substring(0,20);
                                 MilkConstant.EQUIPT=str.substring(8,20);
                                 RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.CONNECT,""));
                                 SpUtil.getInstance().putString(SpUtil.WIFINAME,WifiUtils.shareInstance().getWifiName());
                                 TcpClientManager.getInstance().startTcpClient();
                             }

                         }
                     });


                 }
               }


            }

            @Override
            public void onFinish() {


            }
        }.start();

    }
   private CountDownTimer countDownTimer;
    public static String getHex(String cont){
        char[] chars = cont.toCharArray();
        int i=0;
        int ml=0;
        for(char ca:chars){
            if(i==0){
                ml=HexUtil.toDigit(ca,1)*16;
            }else {
                ml=ml+HexUtil.toDigit(ca,1);
            }
            i++;
        }
        return ml+"";
    }

    public AlarmClock mAlarmClock;
    public void unSppuortSystemFont() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    /**
     * 退出应用
     */
    public void exit(){
        AppManager.getInstance(this).exitAPP(this);
    }

    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.marswin89.marsdaemon.demo:process1",
                Service1.class.getCanonicalName(),
                Receiver1.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.marswin89.marsdaemon.demo:process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }

    class MyDaemonListener implements DaemonConfigurations.DaemonListener{
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }
    @Override
    public void attachBaseContext(Context base){
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
//          ImageLoaderConfiguration.createDefault(this);
        // method.
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.memoryCacheSize(cacheSize);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 10 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//        config.memoryCache(new WeakMemoryCache()).threadPoolSize(1);
        config.memoryCacheExtraOptions(480, 800);
        config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

    }
    public AppComponent getAppComponent(){
        return mAppComponent;
    }
}
