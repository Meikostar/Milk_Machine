package com.canplay.milk.mvp.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.Milk;
import com.canplay.milk.bean.SetMilk;
import com.canplay.milk.mvp.activity.mine.WifiSettingActivity;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.BaseSelector;
import com.canplay.milk.view.HintDialogone;
import com.canplay.milk.view.NavigationBar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;
import socket.bean.MilkConstant;
import socket.util.HexUtil;
import socket.util.WifiUtils;
import udpsocket.core.StaticPackage;
import udpsocket.core.TcpClientManager;
import udpsocket.core.UDPSocketBroadCast;

/**
 * 冲奶设置
 */
public class AddMilkActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_equptname)
    TextView tvEquptname;
    @BindView(R.id.tv_milk)
    TextView tvMilk;
    @BindView(R.id.bs_ml)
    BaseSelector bsMl;
    @BindView(R.id.bs_wd)
    BaseSelector bsWd;
    @BindView(R.id.bs_nd)
    BaseSelector bsNd;


    @Override
    public void initViews() {
        setContentView(R.layout.acitivity_add_milk);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        dialogone = new HintDialogone(this, 2);
        dialogones = new HintDialogone(this, 4);
        dialogones.setBindClickListener(new HintDialogone.BindClickListener() {
            @Override
            public void clicks(int type, int right) {
                sta = 1;
                startTime();
                if (right == 1) {
                    MilkConstant.selectCommnt(2, "3666");
                    TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(), AddMilkActivity.this);
                    startHear();
                } else {
                    isStop = true;
                }
            }
        });
//        mWindowAddPhoto = new PhotoPopupWindow(this);
    }

    private HintDialogone dialogone;
    private HintDialogone dialogones;

    private SetMilk milk = new SetMilk();
   private Subscription mSubscription;
    @Override
    public void bindEvents() {


        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.MILK == bean.type) {
                    String cont= (String) bean.content;
                    if(!TextUtil.isEmpty(cont)){
                        if(cont.contains("开始泡奶")){
                            mHandler.sendEmptyMessage(6);
                            type = 6;
                        }else {
                            showToasts(cont);
                        }
                    }else {
                        mHandler.sendEmptyMessage(6);
                        type = 6;

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

        dialogone.setBindClickListener(new HintDialogone.BindClickListener() {
            @Override
            public void clicks(int type, int right) {
                finish();
            }
        });
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                milk.waterQuantity = bsMl.getSelector();
                milk.consistence = bsNd.getSelector().equals("高") ? "high" : (bsNd.getSelector().equals("中") ? "middle" : "low");
                milk.waterTemperature = bsWd.getSelector();
                showProgress("保存中...");
                presenter.setUserMilkConf(milk.consistence, milk.waterQuantity, milk.waterTemperature);

            }

            @Override
            public void navigationimg() {

            }
        });


        tvMilk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String a1=( Integer.toHexString(Integer.valueOf(bsWd.getSelector())) + "").toUpperCase();
                        String a2=(Integer.toHexString(Integer.valueOf(bsMl.getSelector())) + "").toUpperCase();
                        String a3= (bsNd.getSelector().equals("高") ? "03" : (bsNd.getSelector().equals("中") ? "02" : "01"));
                        String a4= (changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + "";
                        String a5=(getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(bsMl.getSelector())))))) + "").toUpperCase();
//                        String content = Integer.toHexString(Integer.valueOf(bsWd.getSelector())) + ""  +Integer.toHexString(Integer.valueOf(bsMl.getSelector())) + ""+ (bsNd.getSelector().equals("高") ? "03" : (bsNd.getSelector().equals("中") ? "02" : "01")+(changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + ""
//                                +getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(bsMl.getSelector())))))) + "");
//                        String content = Integer.toHexString(Integer.valueOf(bsWd.getSelector())) + "" + Integer.toHexString(Integer.valueOf(bsMl.getSelector())) + "" + (bsNd.getSelector().equals("高") ? "03" : (bsNd.getSelector().equals("中") ? "02" : "01"));
                        String s = (a1+a2+a3+a4+a5);
                        MilkConstant.selectCommnt(3, s);
                        String cont = MilkConstant.sendCommend();
                        if(TextUtil.isEmpty(cont)){
                            startActivity(new Intent(AddMilkActivity.this, WifiSettingActivity.class));

                            return;
                        }
                        TcpClientManager.getInstance().SendMessage(cont, AddMilkActivity.this);



                    }
                }).start();


            }
        });
    }
    @Override
    protected void onResume() {

//        if(TextUtil.isNotEmpty(MilkConstant.HEAD)){
//            TcpClientManager.getInstance().disConnect();
//            StaticPackage.selectPosition=0;
//            TcpClientManager.getInstance().startTcpClient();
//        }else {
//            starBrodcast();
//        }
        super.onResume();
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
    public String getChange(String ml){
        if(TextUtil.isEmpty(ml)){
            return "00";
        }else {
            if(ml.length()==1){
                return "0"+ml;
            }else {
                return ml;

            }
        }
    }
    public int changeInteger(String num){
        int a=0;
        if(TextUtil.isNotEmpty(num)){
            if(isNumeric(num)){
                a=Integer.valueOf(num);
            }else {
                a=0;
            }
        }else {
            a=0;
        }
        return a;
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    private Thread thread;

    public void startHear() {
        if (thread == null) {
            isStop = false;
            thread = new Thread(runHeartbeat);
            thread.start();
        } else {
            thread = null;
            if (isStop) {
                isStop = false;
                thread = new Thread(runHeartbeat);
                thread.start();
            }

        }

    }

    /**
     * 是否完成
     */
    public boolean isStop;
    public boolean isStops=true;
    public Milk mi;
    public int cout;
    public int state;
    private int cont;
    private Runnable runHeartbeat = new Runnable() {
        @Override
        public void run() {
            while (!isStop) {
                if (TextUtil.isNotEmpty(MilkConstant.CONT)) {
                    isStop = true;
                    if(sta!=0&&MilkConstant.CONT.equals("入水温度过高")){
                        conts = MilkConstant.CONT;
                        return;
                    }

                    type = 2;
                    conts = MilkConstant.CONT;
                    mHandler.sendEmptyMessage(1);
                    return;
                }
                if (TextUtil.isNotEmpty(MilkConstant.SB)) {
                    if (MilkConstant.SB.equals("01")) {
                        if (MilkConstant.SEND == 0) {
                            if (sta == 0) {
                                conts = "泡奶成功";
                            } else {
                                conts = "自清洗完成";
                            }

                            type = 3;

//                           showToasts("泡奶成功");
                            mHandler.sendEmptyMessage(1);
//                           dimessProgress();
                            isStop = true;
                            cout = 0;
                        } else {
                            if (state == 2) {
                                String a1=( Integer.toHexString(Integer.valueOf(bsWd.getSelector())) + "").toUpperCase();
                                String a2=(Integer.toHexString(Integer.valueOf(bsMl.getSelector())) + "").toUpperCase();
                                String a3= (bsNd.getSelector().equals("高") ? "03" : (bsNd.getSelector().equals("中") ? "02" : "01"));
                                String a4= (changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + "";
                                String a5=(getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(bsMl.getSelector())))))) + "").toUpperCase();
//                        String content = Integer.toHexString(Integer.valueOf(bsWd.getSelector())) + ""  +Integer.toHexString(Integer.valueOf(bsMl.getSelector())) + ""+ (bsNd.getSelector().equals("高") ? "03" : (bsNd.getSelector().equals("中") ? "02" : "01")+(changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + ""
//                                +getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(bsMl.getSelector())))))) + "");
//                        String content = Integer.toHexString(Integer.valueOf(bsWd.getSelector())) + "" + Integer.toHexString(Integer.valueOf(bsMl.getSelector())) + "" + (bsNd.getSelector().equals("高") ? "03" : (bsNd.getSelector().equals("中") ? "02" : "01"));
                                String s = (a1+a2+a3+a4+a5);
                                MilkConstant.selectCommnt(3, s);
                                String content = Integer.toHexString(Integer.valueOf(bsWd.getSelector())) + ""  +Integer.toHexString(Integer.valueOf(bsMl.getSelector())) + ""+ (bsNd.getSelector().equals("高") ? "03" : (bsNd.getSelector().equals("中") ? "02" : "01")+(changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + ""
                                        +getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(bsMl.getSelector())))))) + "");
//                                String s = content.toUpperCase();
//                                MilkConstant.selectCommnt(3, content);
//                                String content = Integer.toHexString(Integer.valueOf(bsWd.getSelector())) + ""  +Integer.toHexString(Integer.valueOf(bsMl.getSelector())) + "" + (bsNd.getSelector().equals("高") ? "03" : (bsNd.getSelector().equals("中") ? "02" : "01")+(changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + ""
//                                        +getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(bsMl.getSelector())))))) + "");
//                                String content = Integer.toHexString(Integer.valueOf(bsWd.getSelector())) + "" + Integer.toHexString(Integer.valueOf(bsMl.getSelector())) + "" + (bsNd.getSelector().equals("高") ? "03" : (bsNd.getSelector().equals("中") ? "02" : "01"));
//                                MilkConstant.selectCommnt(3, content.toUpperCase());

                                TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(), AddMilkActivity.this);
                            } else {

                                if (cont == 0) {
                                    sta = 0;
                                    conts = "开始泡奶...";
                                    type = 1;
                                    mHandler.sendEmptyMessage(1);
//                                   showProgress("开始泡奶...");
                                    cont++;
                                }

                            }

                            state = 0;
                        }

                    } else if (MilkConstant.SB.equals("02")) {

//                        if (cont == 0) {
//                            conts = "设备需要清洗，开始清洗 ...";
                        if (sta == 0) {
                            type = 7;
                            mHandler.sendEmptyMessage(1);
                        }
////                           showProgress("设备需要清洗，开始清洗 ...");
//                            cont++;
//                        }

//
//                        MilkConstant.selectCommnt(2, "3666");
//                        TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(), AddMilkActivity.this);

                    } else if (MilkConstant.SB.equals("03")) {
                        if (cont == 0) {
                            conts = "设备清洗中...";
                            type = 1;
                            mHandler.sendEmptyMessage(1);
//                           showProgress("设备清洗中...");
                            cont++;
                        }

                        state = 2;


                    } else if (MilkConstant.SB.equals("04")) {

                            sta = 0;
                            conts = "泡奶中...";
                            type = 1;
                            mHandler.sendEmptyMessage(1);
//                           showProgress("泡奶中...");
                            cont++;


                    } else if (MilkConstant.SB.equals("05")) {
//                       dimessProgress();
                        conts = "正在冲水中请稍后";
                        type = 4;
                        mHandler.sendEmptyMessage(1);
//                       showToasts("正在冲水中请稍后");
                        isStop = true;
                    }
                }
                MilkConstant.selectCommnt(1, "");
                TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommends(), AddMilkActivity.this);
                try {
                    Thread.sleep(1000);// 正常
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    public String conts;
    public int type;

    @Override
    public boolean handleMessage(Message msg) {

        if (type == 1) {
            if (!isFinishing()) {
                showProgress(conts);
            }

        } else if (type == 2) {
            dimessProgress();
            showToasts(conts);

        } else if (type == 6) {
            type = 0;
            if (TextUtil.isNotEmpty(MilkConstant.HEAD)) {
                startHear();

            }

        } else if (type == 7) {
            type = 0;

            isStop = true;
            dialogones.show(tvEquptname);
        } else if (type == 3) {

            if (sta == 0) {
                RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.MILKS,bsMl.getSelector()));
                dimessProgress();
                showToasts("泡奶成功");
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
//                BaseApplication.isReself=1;
//                String contents = "66";
//                MilkConstant.selectCommnt(9, contents.toUpperCase());
//                TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(), AddMilkActivity.this);
//
//                if(isStops){
//                    isStops=false;
//                    isFinish();
//                }
            } else {
                dimessProgress();
                showToasts(conts);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }

        } else {

            dimessProgress();
            showToasts(conts);
        }
        return super.handleMessage(msg);
    }
    private CountDownTimer countDownTimer1;
   private int couts;
    public void isFinish() {
        couts=0;
        countDownTimer1 = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                couts++;
//                if(couts==4)
//                {
//                    presenter.insertUserMilkRecord(bsMl.getSelector() + "");
//                }

                if (BaseApplication.isReself != 0) {
                    String contents = "66";
                    MilkConstant.selectCommnt(9, contents.toUpperCase());
                    TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(), AddMilkActivity.this);

                }else {
                    countDownTimer1.cancel();
                    dimessProgress();
                    showToasts("泡奶成功");
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }

            }

            @Override
            public void onFinish() {
                if (BaseApplication.isReself != 0) {

                    dimessProgress();
                    showToasts("泡奶成功");
                    finish();
                }

            }
        }.start();
    }
    private CountDownTimer countDownTimer2;
    private int sta = 0;

    public void startTime() {
        countDownTimer2 = new CountDownTimer(50000, 10000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                sta = 0;
                countDownTimer2.cancel();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        BaseApplication.isReself=0;
        super.onDestroy();
    }

    @Override
    public void initData() {
        boolean check;
        SetMilk milk = SpUtil.getInstance().getMilk();
        bsMl.setData(2, milk.waterQuantity);
        bsNd.setData(1, milk.consistence);
        if (Integer.valueOf(milk.waterTemperature) < 40 && Integer.valueOf(milk.waterTemperature) > 50) {
            check = false;
        } else {
            check = true;
        }
        bsWd.setData(0,  milk.waterTemperature );
    }


    @Override
    public <T> void toEntity(T entity, int type) {
        dimessProgress();
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.REFEST_SET, ""));
        dialogone.show(findViewById(R.id.line));
    }

    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {
        dimessProgress();
        showToasts(msg);
    }
}
