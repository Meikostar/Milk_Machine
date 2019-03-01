package com.canplay.milk.mvp.activity.mine;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.USER;
import com.canplay.milk.mvp.activity.account.LoginActivity;
import com.canplay.milk.mvp.activity.home.OutWatherActivity;
import com.canplay.milk.mvp.activity.home.PushMilkActivity;
import com.canplay.milk.permission.PermissionConst;
import com.canplay.milk.permission.PermissionFail;
import com.canplay.milk.permission.PermissionGen;
import com.canplay.milk.permission.PermissionSuccess;
import com.canplay.milk.receiver.NetworkConnectChangedReceiver;
import com.canplay.milk.util.ConfigUtils;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.MCheckBox;
import com.canplay.milk.view.NavigationBar;
import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.task.__IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.EspAES;
import com.espressif.iot.esptouch.util.EspNetUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;
import socket.bean.MilkConstant;
import socket.util.HexUtil;
import socket.util.WifiUtils;
import udpsocket.core.TcpClientManager;
import udpsocket.core.UDPSocketBroadCast;

/**
 * wifi设置界面
 */
public class WifiSettingActivity extends BaseActivity {


    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_wifi)
    TextView tvWifi;
    @BindView(R.id.iv_wifi)
    MCheckBox ivWifi;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_equptname)
    TextView tvEquptname;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_login_status)
    TextView tvLoginStatus;
    @BindView(R.id.tv_get)
    TextView tvGet;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    @BindView(R.id.tv_open)
    TextView tvOpen;
    @BindView(R.id.et_wifi)
    ClearEditText etWifi;

    @Override
    public void initViews() {
        setContentView(R.layout.acitivity_wifi_setting);
        ButterKnife.bind(this);
        navigationBar.setNavigationBarListener(this);

        /**
         * 获取WIFI服务
         */
        boolean useable = WifiUtils.shareInstance().isUseable();
        if (useable) {
            ivWifi.setChecked(true);
        } else {
            ivWifi.setChecked(false);
        }
        if(TextUtil.isNotEmpty(MilkConstant.HEAD)){
            tvStatus.setText("设备已连接");
            tvLoginStatus.setText("已登录");
            img.setImageResource(R.drawable.gou);
        }else {
            tvStatus.setText("设备未连接");
            tvLoginStatus.setText("未登录");
            img.setImageResource(R.drawable.gou1);
        }
        if(TextUtil.isNotEmpty(MilkConstant.EQUIPT)){
            tvEquptname.setText("泡奶机:  "+MilkConstant.EQUIPT);
        }
       USER user= SpUtil.getInstance().getUsers();

        tvName.setText(WifiUtils.shareInstance().getWifiName());
        WIFI_NAME=WifiUtils.shareInstance().getWifiName();
        if(TextUtil.isNotEmpty(SpUtil.getInstance().getString(WIFI_NAME))){
            etWifi.setText(SpUtil.getInstance().getString(WIFI_NAME));
        }   if(TextUtil.isNotEmpty(user.name)){
            tvUser.setText("用户名: "+user.name);
        }

    }
    private EsptouchAsyncTask4 mTask;
    @Override
    public void bindEvents() {
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseApplication.stop=false;
               if(WifiUtils.shareInstance().isUseable()){
                   BaseApplication.state=1;
                   startSearch();
                   if(TcpClientManager.getInstance().isConnected()){
                        TcpClientManager.getInstance().disConnect();
                       TcpClientManager.getInstance().startTcpClient();
                   }else {


                       if(TextUtil.isNotEmpty(MilkConstant.HEAD)){
                           TcpClientManager.getInstance().disConnect();
                           TcpClientManager.getInstance().startTcpClient();
                       }else {
                           starBrodcast();

                       }

                   }
               }else {
                   showToasts("请打开wifi");
               }
            }
        });
        ivWifi.setOnCheckClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionGen.with(WifiSettingActivity.this)
                        .addRequestCode(PermissionConst.WIFI_DATE)
                        .permissions(
                                Manifest.permission.ACCESS_WIFI_STATE,
                                Manifest.permission.CHANGE_WIFI_STATE)
                        .request();

            }
        });
        tvOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(st==1){
                    showToasts("设备已断开");
                    return;
                }
                if(TextUtil.isNotEmpty(MilkConstant.HEAD)){

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String phone = SpUtil.getInstance().getString(SpUtil.PHONE);

                            String content = "";
                            for(int i=0;i<phone.length();i++){
                                content=content+ "0"+Integer.toHexString(Integer.valueOf(phone.substring(i,i+1)));
                            }
                            MilkConstant.selectCommnt(5,content);
                            Looper.prepare();
                            TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(),WifiSettingActivity.this);
                            Looper.loop();// 进入loop中的循环，查看消息队列

                        }
                    }).start();
                    showProgress("设备正在断开中");
                    TcpClientManager.getInstance().disConnect();
                    stopConnct();

                    MilkConstant.HEAD="";
                    BaseApplication.stop=true;

                }else {
                    showToasts("设备未连接");
                }
          }
        });
        tvGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtil.isEmpty(etWifi.getText().toString())){
                    showToasts("请输入WiFi密码");
                    return;
                }
                if (state==1) {
                    startW();
                    return;
                }
                if (TextUtil.isNotEmpty(MilkConstant.HEAD)) {
                    startW();
                    return;
                }
                BaseApplication.stop=false;
                setConnect();
                starBrodcast();
            }
        });

    }
    @PermissionSuccess(requestCode = PermissionConst.WIFI_DATE)
    public void requestSdcardSuccess() {
        if (ivWifi.isCheck()) {
            WifiUtils.shareInstance().closeWifi();
            tvName.setText(WifiUtils.shareInstance().getWifiName());
            ivWifi.setChecked(false);
            tvName.setText("");
            etWifi.setVisibility(View.INVISIBLE);
        } else {
            tvName.setText(WifiUtils.shareInstance().getWifiName());
            etWifi.setVisibility(View.VISIBLE);
            WifiUtils.shareInstance().openWifi();
            ivWifi.setChecked(true);
        }
    }

    @PermissionFail(requestCode = PermissionConst.WIFI_DATE)
    public void requestSdcardFailed() {
        showToasts("获取权限失败");
    }
    public void starBrodcast(){
        UDPSocketBroadCast broadCast=new UDPSocketBroadCast();
        broadCast.startUDP(new UDPSocketBroadCast.UDPDataCallBack() {
            @Override
            public void mCallback(String str) {
                BaseApplication.ip= getHex(str.substring(22,24))+"."+getHex(str.substring(24,26))+"."+getHex(str.substring(26,28))+"."+getHex(str.substring(28,30));

                if(TextUtil.isNotEmpty(BaseApplication.ip)){
                    MilkConstant.HEAD=str.substring(0,20);
                    MilkConstant.HD=str.substring(0,20);
                    MilkConstant.EQUIPT=str.substring(8,20);
                    TcpClientManager.getInstance().startTcpClient();
                }
            }
        });
    }
    public void setConnect(){

        String ssd = tvName.getText().toString();
        String psw = etWifi.getText().toString();
        String bssid1 = WifiUtils.shareInstance().getBSSID();
        byte[] ssid =ByteUtil.getBytesByString(ssd);
        byte[] password = ByteUtil.getBytesByString(psw);
        byte [] bssid = EspNetUtil.parseBssid2bytes(bssid1);
        byte[] deviceCount = "1".getBytes();

        if(mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask4(WifiSettingActivity.this);
        mTask.execute(ssid, bssid, password, deviceCount);
    }
    public static String getHex(String cont){
        char[] chars = cont.toCharArray();
        int i=0;
        int ml=0;
        for(char ca:chars){
            if(i==0){
                ml= HexUtil.toDigit(ca,1)*16;
            }else {
                ml=ml+HexUtil.toDigit(ca,1);
            }
            i++;
        }
        return ml+"";
    }

    @Override
    public boolean handleMessage(Message msg) {

        if(TextUtil.isNotEmpty(MilkConstant.EQUIPT)){
            tvEquptname.setText("泡奶机:  "+MilkConstant.EQUIPT);
        }
        tvName.setText(WifiUtils.shareInstance().getWifiName());
        return super.handleMessage(msg);
    }

    /**
     * 心跳测试线程
     */
    private Runnable runHeartbeat = new Runnable() {
        @Override
        public void run() {

            while (!isStop) {
                mHandler.sendEmptyMessage(1);
                try {
                    Thread.sleep(2000);// 正常
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };



    private Subscription mSubscription;
    private int stu;
    private int cout;
    private boolean isStop;
    private int types;

    @Override
    public void initData() {
       new Thread(runHeartbeat).start();
        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.BIND == bean.type) {
                    dimessProgress();
                    stu=1;
                   if(cout==0){
                       star();
                       cout++;
                   }


                    String content = (String) bean.content;
//                    showToasts(content);
                } else if (SubscriptionBean.OFF == bean.type) {
                    dimessProgress();
                    tvStatus.setText("设备已连接");
                    tvLoginStatus.setText("已登录");
                    stu=1;
                    if(cout==0){
                        star();
                        cout++;
                    }

                } else if (SubscriptionBean.CONNECT == bean.type) {
                    if(WifiSettingActivity.this!=null){
                       return;
                    }
                    if(mTask!=null){
                        if(mTask.mProgressDialog!=null){
                            mTask.mProgressDialog.dismiss();
                        } if(mTask.mResultDialog!=null){
                            mTask.mResultDialog.dismiss();
                        }
                    }



                    if(TextUtil.isNotEmpty(MilkConstant.EQUIPT)){
                        tvEquptname.setText("泡奶机:  "+MilkConstant.EQUIPT);
                        tvEquptname.setVisibility(View.VISIBLE);
                    }
                    tvStatus.setText("设备已连接");
                    tvLoginStatus.setText("已登录");
                    img.setImageResource(R.drawable.gou);
                    SpUtil.getInstance().putString(WIFI_NAME,etWifi.getText().toString().trim());
                    SpUtil.getInstance().putString(SpUtil.WIFINAME,WIFI_NAME);
                    types=1;

                } else if (SubscriptionBean.WIFISTATE == bean.type) {
                   String cont= (String) bean.content;
                    if(cont.equals("1")){
                        if(type==0){
                            showToasts("WiFi链接断开");
                        }
                        type++;
                        tvStatus.setText("设备未连接");
                        tvLoginStatus.setText("未登录");


                        tvName.setText("");
                        state=0;
                        etWifi.setVisibility(View.INVISIBLE);

                    }else if(cont.equals("2")){
                        img.setImageResource(R.drawable.gou);
                        tvName.setText(WifiUtils.shareInstance().getWifiName());
                        ivWifi.setChecked(true);
                        etWifi.setVisibility(View.VISIBLE);
                        type=0;


                    }else if(cont.equals("3")){
                        tvStatus.setText("设备未连接");
                        tvLoginStatus.setText("未登录");
                        tvName.setText(WifiUtils.shareInstance().getWifiName());
                        ivWifi.setChecked(false);
                        tvName.setText("");
                        etWifi.setVisibility(View.INVISIBLE);
//                        showToasts("WiFi已关闭");
                    }


                }else  if (SubscriptionBean.WAIT == bean.type) {
                    String cont= (String) bean.content;
                    showToasts(cont);

                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        RxBus.getInstance().addSubscription(mSubscription);
    }
    private int counts;
    @Override
    protected void onDestroy() {
        super.onDestroy();



    }
    private int type=0;
    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text ="正在配置中...";
                Toast.makeText(WifiSettingActivity.this, text,
                        Toast.LENGTH_LONG).show();
            }

        });
    }
    private static CountDownTimer countDownTim;
    public  void star(){

        if (countDownTim != null) {
            countDownTim.cancel();
        }
        countDownTim = new CountDownTimer(3*1000,3*1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {


                if(mSearchDialogs!=null){
                    mSearchDialogs.dismiss();
                }
                cout=0;
                if(countDownTi==null){
//                    showToasts("设备连接成功");
                }else {
                    countDownTi.cancel();
                    countDownTi=null;
                }


            }
        }.start();
    }
    private static CountDownTimer countDownTi;
    private int st;
    public  void stopConnct(){
        st=1;
        if (countDownTi != null) {
            countDownTi.cancel();
        }
        countDownTi = new CountDownTimer(3*1000,3*1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                    dimessProgress();
                    showToasts("设备已断开");



            }
        }.start();
    }
    private static CountDownTimer countDownTimer;
    public  void startSearch(){
        showSearchDialiog();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(15*1000,15*1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                if(mSearchDialogs!=null){
                    mSearchDialogs.dismiss();
                }
                if(stu!=0){
                    stu=0;
                }else {
                    showToasts("未搜索到相关设备");
                }


            }
        }.start();
    }
    private static CountDownTimer countDownTimer3;
    private String receiver;
    public  void startW(){
        showDialiog();

            final UDPSocketBroadCast broadCast = new UDPSocketBroadCast();
            broadCast.startUDP(new UDPSocketBroadCast.UDPDataCallBack() {
                @Override
                public void mCallback(String str) {
                    receiver=str;
                    SpUtil.getInstance().putString(WIFI_NAME,etWifi.getText().toString().trim());
                    showToasts("配置成功");
                    if(mProgressDialogs!=null){
                        mProgressDialogs.dismiss();
                    }

                }
            });


        if (countDownTimer3 != null) {
            countDownTimer3.cancel();
        }
        countDownTimer3 = new CountDownTimer(4*1000,4*1000) {
            @Override
            public void onTick(long millisUntilFinished) {
              if(TextUtil.isEmpty(receiver)){
                  if(mProgressDialogs!=null){
                      mProgressDialogs.dismiss();
                  }
                  setConnect();
              }
            }

            @Override
            public void onFinish() {


            }
        }.start();
    }
    public ProgressDialog mProgressDialogs;
    public void showDialiog(){

        mProgressDialogs = new ProgressDialog(this);
        mProgressDialogs.setMessage("设备连接wifi中...");
        mProgressDialogs.setCanceledOnTouchOutside(false);
        mProgressDialogs.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        mProgressDialogs.setButton(DialogInterface.BUTTON_NEGATIVE, this.getText(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       mProgressDialogs.dismiss();
                    }
                });
        mProgressDialogs.show();
    }
    public ProgressDialog mSearchDialogs;
    public void showSearchDialiog(){

        mSearchDialogs = new ProgressDialog(this);
        mSearchDialogs.setMessage("搜索设备中...");
        mSearchDialogs.setCanceledOnTouchOutside(false);
        mSearchDialogs.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        mSearchDialogs.setButton(DialogInterface.BUTTON_NEGATIVE, this.getText(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSearchDialogs.dismiss();
                    }
                });
        mSearchDialogs.show();
    }
    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };

    private static final String TAG = "EsptouchDemoActivity";
    private static String WIFI_NAME = "";

    private  final boolean AES_ENABLE = false;
    private  final String AES_SECRET_KEY = "1234567890123456"; // TODO modify your own key
    private  class EsptouchAsyncTask4 extends AsyncTask<byte[], Void, List<IEsptouchResult>> {
        private WeakReference<WifiSettingActivity> mActivity;

        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();
        private ProgressDialog mProgressDialog;
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;

        EsptouchAsyncTask4(WifiSettingActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        void cancelEsptouch() {
            cancel(true);
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
            if (mResultDialog != null) {
                mResultDialog.dismiss();
            }
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
        }

        @Override
        protected void onPreExecute() {
            Activity activity = mActivity.get();
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("设备连接wifi中...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        if (__IEsptouchTask.DEBUG) {
//                            Log.i(TAG, "progress dialog back pressed canceled");
                        }
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            synchronized (mLock) {
                                if (__IEsptouchTask.DEBUG) {
//                                    Log.i(TAG, "progress dialog cancel button canceled");
                                }
                                if (mEsptouchTask != null) {
                                    mEsptouchTask.interrupt();
                                }
                            }
                        }
                    });
            mProgressDialog.show();
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            WifiSettingActivity activity = mActivity.get();
            int taskResultCount;
            synchronized (mLock) {
                // !!!NOTICE
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                byte[] deviceCountData = params[3];
                taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
                Context context = activity.getApplicationContext();
                if (AES_ENABLE) {
                    byte[] secretKey = AES_SECRET_KEY.getBytes();
                    EspAES aes = new EspAES(secretKey);
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, aes, context);
                } else {
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, null, context);
                }
                mEsptouchTask.setEsptouchListener(activity.myListener);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            WifiSettingActivity activity = mActivity.get();
            mProgressDialog.dismiss();
            if(WifiSettingActivity.this!=null){
                return;
            }
            mResultDialog = new AlertDialog.Builder(activity)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
            mResultDialog.setCanceledOnTouchOutside(false);
            if (result == null) {
                if(type!=0){
                    return;
                }
                mResultDialog.setMessage("配置失败");
                mResultDialog.show();
                return;
            }

            IEsptouchResult firstResult = result.get(0);
            // check whether the task is cancelled and no results received
            if (!firstResult.isCancelled()) {
                int count = 0;
                // max results to be displayed, if it is more than maxDisplayCount,
                // just show the count of redundant ones
                final int maxDisplayCount = 5;
                // the task received some results including cancelled while
                // executing before receiving enough results
                if (firstResult.isSuc()) {
                    StringBuilder sb = new StringBuilder();
                    for (IEsptouchResult resultInList : result) {
//                        sb.append("Esptouch success, bssid = ")
//                                .append(resultInList.getBssid())
//                                .append(", InetAddress = ")
//                                .append(resultInList.getInetAddress().getHostAddress())
//                                .append("\n");
                        count++;
                        if(TextUtil.isNotEmpty(resultInList.getInetAddress().getHostAddress())){
                            ip=resultInList.getInetAddress().getHostAddress();
                            BaseApplication.ip=resultInList.getInetAddress().getHostAddress();
                            SpUtil.getInstance().putIp(ip);
                            SpUtil.getInstance().putString(WIFI_NAME,etWifi.getText().toString().trim());
                        }

                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    if (count < result.size()) {
                        sb.append("\nthere's ")
                                .append(result.size() - count)
                                .append(" more result(s) without showing\n");
                    }
                    mResultDialog.setMessage("配置成功");
                    state=1;


                } else {
                    if(type!=0){
                        return;
                    }
                    mResultDialog.setMessage("配置失败");
                }
                if(WifiSettingActivity.this!=null&&mResultDialog!=null){
                    mResultDialog.show();
                }

            }

            activity.mTask = null;
        }
    }
    public static  String  ip="";

    private int state;


    private static void reconnectLanSvr() {



    }
}
