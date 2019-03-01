package com.canplay.milk.mvp.activity;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;


import com.canplay.medical.R;

import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.BaseDailogManager;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.BASE;
import com.canplay.milk.bean.MilkInfo;
import com.canplay.milk.bean.SetMilk;
import com.canplay.milk.fragment.FileFragment;
import com.canplay.milk.fragment.HomeFragment;
import com.canplay.milk.fragment.SetFragment;
import com.canplay.milk.fragment.WikiPediaFragment;
import com.canplay.milk.mvp.activity.account.LoginActivity;
import com.canplay.milk.mvp.activity.home.AddMilkActivity;
import com.canplay.milk.mvp.activity.home.PushMilkActivity;
import com.canplay.milk.mvp.activity.mine.WifiSettingActivity;
import com.canplay.milk.mvp.adapter.FragmentViewPagerAdapter;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.permission.PermissionConst;
import com.canplay.milk.permission.PermissionGen;
import com.canplay.milk.permission.PermissionSuccess;
import com.canplay.milk.receiver.Service1;
import com.canplay.milk.util.ConfigUtils;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.BottonNevgBar;
import com.canplay.milk.view.ChangeNoticeDialog;
import com.canplay.milk.view.HintDialogone;
import com.canplay.milk.view.MarkaBaseDialog;
import com.canplay.milk.view.NoScrollViewPager;

import com.google.zxing.client.android.activity.CaptureActivity;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import rx.Subscription;
import rx.functions.Action1;
import socket.bean.MilkConstant;
import socket.core.ConnectManager;
import socket.util.HexUtil;
import socket.util.WifiUtils;
import udpsocket.core.SocketClientManager;
import udpsocket.core.StaticPackage;
import udpsocket.core.TcpClientManager;
import udpsocket.core.UDPSocketBroadCast;

import android.content.DialogInterface;

import javax.inject.Inject;


public class MainActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;
    NoScrollViewPager viewpagerMain;
    BottonNevgBar bnbHome;
    private Subscription mSubscription;
    private FragmentViewPagerAdapter mainViewPagerAdapter;
    private List<Fragment> mFragments;
    private int current = 0;
    private long firstTime = 0l;
    private SetFragment setFragment;
    private FileFragment fileFragment;
    private HomeFragment homeFragment;
    private WikiPediaFragment wikiPediaFragment;
    private View line;
    private ChangeNoticeDialog dialog;
    private MarkaBaseDialog exitDialog;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_main);
        bnbHome = (BottonNevgBar) findViewById(R.id.bnb_home);
        line = findViewById(R.id.line);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        presenter.getUserMilkConf();
        presenter.getNextUserVaccineInfo();
        PermissionGen.with(MainActivity.this)
                .addRequestCode(PermissionConst.REQUECT_DATE)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
        exitDialog = BaseDailogManager.getInstance().getBuilder(this).setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    dialog.dismiss();
                    finish();
                } else {
                    dialog.dismiss();
                }
            }
        }).create();
        viewpagerMain = (NoScrollViewPager) findViewById(R.id.viewpager_main);
        viewpagerMain.setScanScroll(false);
        dialogone = new HintDialogone(this, 3);
        dialog = new ChangeNoticeDialog(this, line);

    }

    private HintDialogone dialogone;

    private void alarm() {
        startService(new Intent(MainActivity.this, Service1.class));

    }

    private int cout = 0;

    @Override
    public boolean handleMessage(Message msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                String contents = "0112";
//                MilkConstant.selectCommnt(9,contents.toUpperCase());
//                TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(),MainActivity.this);
            }
        }).start();

        return super.handleMessage(msg);
    }
    private int sta=0;
    private CountDownTimer countDownTimer2;
    public void startTime(){
        countDownTimer2 = new CountDownTimer(4000, 4000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                sta=0;
                countDownTimer2.cancel();
            }
        }.start();
    }
    private long nowTime;
    private long pastTime;
    @Override
    public void bindEvents() {
        dialogone.setBindClickListener(new HintDialogone.BindClickListener() {
            @Override
            public void clicks(int type, int right) {
                if (right == 1) {
                    Intent intent = new Intent(MainActivity.this, WifiSettingActivity.class);
                    startActivity(intent);

                }

            }
        });
        setViewPagerListener();
        setNevgBarChangeListener();

        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.MENU_SCAN == bean.type) {
                    dialog.show();
                } else if (SubscriptionBean.FINISH == bean.type) {
                    finish();
                } else if (SubscriptionBean.REFEST_SET == bean.type) {
                    presenter.getUserMilkConf();

                } else if (SubscriptionBean.MILKS == bean.type) {
                    if(sta==0){
                        sta=1;
                        String mls= (String) bean.content;
                        presenter.insertUserMilkRecord(mls + "");
                    }
                    startTime();
                } else if (SubscriptionBean.REFESH_TIME == bean.type) {
                    presenter.getNextUserVaccineInfo();
                    presenter.getUserMilkConf();
                } else if (SubscriptionBean.RESTART == bean.type) {
                    if(WifiUtils.shareInstance().isUseable()){
                        String name = SpUtil.getInstance().getString(SpUtil.WIFINAME);
                        String wifiName = WifiUtils.shareInstance().getWifiName();
                        if(name.equals(wifiName)){
//                            if (!dialogone.isShow()) {
//                                dialogone.show(line);
//                            }
                            if (WifiUtils.shareInstance().isUseable()) {
                                TcpClientManager.getInstance().disConnect();
                                StaticPackage.selectPosition=0;
                                TcpClientManager.getInstance().startTcpClient();
                            }
                        }
                    }else {
                        if (!dialogone.isShow()) {
                            dialogone.show(line);
                        }
                    }



                } else if (SubscriptionBean.OFF == bean.type) {
                    dialogone.dismiss();
                }  else if (SubscriptionBean.NEWINSTANCES == bean.type) {

                    setCallBack((String)bean.content);
                } else if (SubscriptionBean.WIFISTATE == bean.type) {
                    nowTime = System.currentTimeMillis();
                    String type = (String) bean.content;

                    if (pastTime == 0) {
                        pastTime = nowTime;
                        starBoard(type);
                    } else {
                        if ((nowTime - pastTime) > 10 * 1000) {
                            pastTime = nowTime;
                            starBoard(type);
                        } else {
                            pastTime = 0;
                        }
                    }

                }


            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        RxBus.getInstance().addSubscription(mSubscription);

        dialog.setBindClickListener(new ChangeNoticeDialog.BindClickListener() {
            @Override
            public void teaMoney(String money) {
                dialog.dismiss();
            }
        });
    }

    public static String getHex(String cont) {
        char[] chars = cont.toCharArray();
        int i = 0;
        int ml = 0;
        for (char ca : chars) {
            if (i == 0) {
                ml = HexUtil.toDigit(ca, 1) * 16;
            } else {
                ml = ml + HexUtil.toDigit(ca, 1);
            }
            i++;
        }
        return ml + "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TextUtil.isNotEmpty(MilkConstant.HEAD)){
            TcpClientManager.getInstance().disConnect();
            StaticPackage.selectPosition=0;
            TcpClientManager.getInstance().startTcpClient();
        }else {
            starBrodcast();
        }
        starBoard("2");
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
    public void starBoard(String type) {

        String name = SpUtil.getInstance().getString(SpUtil.WIFINAME);
        String wifiName = WifiUtils.shareInstance().getWifiName();
        if(TextUtil.isEmpty(name)||TextUtil.isEmpty(wifiName)){
            return;
        }
        if (TextUtil.isNotEmpty(type) && type.equals("2"))

            if (WifiUtils.shareInstance().isUseable()&&name.equals(wifiName)&&!TcpClientManager.getInstance().isConnected()) {

                final UDPSocketBroadCast broadCast = new UDPSocketBroadCast();
                broadCast.startUDP(new UDPSocketBroadCast.UDPDataCallBack() {
                    @Override
                    public void mCallback(String str) {
                        BaseApplication.ip = getHex(str.substring(22, 24)) + "." + getHex(str.substring(24, 26)) + "." + getHex(str.substring(26, 28)) + "." + getHex(str.substring(28, 30));

                        if (TextUtil.isNotEmpty(BaseApplication.ip)) {

                            MilkConstant.HEAD = str.substring(0, 20);
                            MilkConstant.HD = str.substring(0, 20);
                            MilkConstant.EQUIPT = str.substring(8, 20);
                            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.CONNECT, ""));
                            SpUtil.getInstance().putString(SpUtil.WIFINAME,WifiUtils.shareInstance().getWifiName());
                            if(!TcpClientManager.getInstance().isConnected()){
                                TcpClientManager.getInstance().startTcpClient();
                            }

                        }

                    }
                });


            }
    }

    @Override
    public void initData() {
        addFragment();
        mainViewPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mFragments);
        viewpagerMain.setAdapter(mainViewPagerAdapter);
        viewpagerMain.setOffscreenPageLimit(3);//设置缓存view 的个数
        viewpagerMain.setCurrentItem(current);
        bnbHome.setSelect(current);


    }
    private boolean isSuccess=true;
    private CountDownTimer countDownTimers;
    public void setCallBack(String content){


                    String commend = "";
                    String cont = "";
                    String data = MilkConstant.MilkState(content);
                    String[] split = data.split("#");
                    commend = split[0];

                    if (split.length == 2) {
                        cont = split[1];
                    }
                    if (commend.equals("A3")) {
//                        mHandler.sendMessageDelayed(new Message(),3000);

                            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.MILK, cont));


                    } else if (commend.equals("A2")) {

                        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.CLERS, cont));
                    }  else if (commend.equals("A4")) {
                        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.WATER, cont));

                    }else if (commend.equals("A8")) {
                        if (TextUtil.isNotEmpty(cont)) {
                            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.BIND, cont));


                        } else {
                            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.BIND, "绑定成功"));
                        }

                    } else if (commend.equals("A9")) {
//                        BaseApplication.isReself=0;
//                        String name = cont.substring(0, 2);
//                        char[] chars = name.toCharArray();
//                        int i = 0;
//                        int ml = 0;
//                        for (char ca : chars) {
//                            if (i == 0) {
//                                ml = HexUtil.toDigit(ca, 1) * 16;
//                            } else {
//                                ml = ml + HexUtil.toDigit(ca, 1);
//                            }
//                            i++;
//                        }
//                            if(countDownTimers!=null){
//                               countDownTimers.cancel();
//                            }
//                             countDownTimers = new CountDownTimer(5000, 5000) {
//                            @Override
//                            public void onTick(long millisUntilFinished) {
//
//
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                isSuccess=true;
//                                countDownTimers.cancel();
//                            }
//                        }.start();
//                        if(isSuccess){
//                            isSuccess=false;
//                            presenter.insertUserMilkRecord(ml + "");
//                        }

                    } else if (commend.equals("A5")) {
                        if (TextUtil.isNotEmpty(cont)) {
                            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.BIND, cont));
                        } else {
                            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.BIND, "解绑成功"));
                        }
                    } else if (commend.equals("A1")) {
                        if (BaseApplication.send == 1) {
                            if (TextUtil.isNotEmpty(cont)) {
//                                showToasts(cont);

                            }

                            BaseApplication.send = 0;
                        } else if (BaseApplication.send == 2) {
                            if (TextUtil.isNotEmpty(cont)) {
//                                showToasts(cont);

                            }
                            BaseApplication.send = 3;
                        }
                    }

                    if (cont.equals("绑定成功")) {
//                      TcpClientManager.getInstance().startHear();
                        BaseApplication.bind = true;
                    } else if (cont.equals("绑定失败")) {
//                        showToasts("其他设备正在操作");
                        BaseApplication.bind = false;
                        return;
                    } else if (cont.equals("解绑成功")) {
//                        showToasts("其他设备正在操作");
                        BaseApplication.bind = false;
                        return;
                    } else if (cont.equals("解绑失败")) {
                        showToasts("解绑失败请重试");
                        return;
                    }
//                    if(TextUtil.isNotEmpty(cont)){
//                        if(BaseApplication.send==1){
//                            showToasts(cont);
//                            BaseApplication.send=0;
//                        }else  if(BaseApplication.send==2){
//                            showToasts(cont);
//                            BaseApplication.send=3;
//                        }
//
//                    }



    }

    private void setViewPagerListener() {
        viewpagerMain.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bnbHome.setSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setNevgBarChangeListener() {
        bnbHome.setOnChangeListener(new com.canplay.milk.view.OnChangeListener() {
            @Override
            public void onChagne(int currentIndex) {
                current = currentIndex;
                bnbHome.setSelect(currentIndex);
                alarm();
                viewpagerMain.setCurrentItem(currentIndex);
            }
        });
    }

    private void addFragment() {
        mFragments = new ArrayList<>();

        setFragment = new SetFragment();
        fileFragment = new FileFragment();
        homeFragment = new HomeFragment();
        wikiPediaFragment = new WikiPediaFragment();
        mFragments.add(homeFragment);
        mFragments.add(wikiPediaFragment);
        mFragments.add(fileFragment);
        mFragments.add(setFragment);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (exitDialog != null) {
            exitDialog.dismiss();
        }
    }

    @PermissionSuccess(requestCode = PermissionConst.REQUECT_CODE_CAMERA)
    public void requestSdcardSuccess() {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
         /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
         * 也可以不传这个参数
         * 不传的话  默认都为默认不震动  其他都为true
         * */

        //ZxingConfig config = new ZxingConfig();
        //config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
        //config.setPlayBeep(true);//是否播放提示音
        //config.setShake(true);//是否震动
        //config.setShowAlbum(true);//是否显示相册
        //config.setShowFlashLight(true);//是否显示闪光灯
        //intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    private int REQUEST_CODE_SCAN = 6;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra("scan_result");
                showToasts("扫描结果为：" + content);
//                result.setText("扫描结果为：" + content);
            }
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exitDialog.show();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    @Override
    public <T> void toEntity(T entity, int type) {
        if (type == 7) {
            BASE base = (BASE) entity;
            SpUtil.getInstance().putBASE(base);
        } else if (type == 8) {
            MilkInfo info = (MilkInfo) entity;
            if(info!=null){
                if(!TextUtil.isEmpty(info.milkWeight)){
                    SpUtil.getInstance().putString(SpUtil.WEIGHT_W,info.milkWeight);
                    if(TextUtil.isNotEmpty(info.waterQuantity)){
                        if(isNumeric(info.waterQuantity)&&isNumeric(info.milkWeight)){
                           int a=Integer.valueOf( info.milkWeight);
                           double b=Integer.valueOf( info.waterQuantity)*1.0;
                            SpUtil.getInstance().putString(SpUtil.PER,(a/b)+"");
                        }
                    }


                }if(!TextUtil.isEmpty(info.grade)){
                    SpUtil.getInstance().putString(SpUtil.WEIGHT_D,info.grade);
                }
            }
        } else {
            SetMilk milk = (SetMilk) entity;
            if(milk!=null){
                presenter.getMilkInfo(milk.milkInfoId);
                SpUtil.getInstance().putMilk(milk);
            }

        }

    }

    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {
        showToasts(msg);
    }
}
