package com.canplay.milk.mvp.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.Milk;
import com.canplay.milk.bean.SetMilk;
import com.canplay.milk.mvp.activity.MainActivity;
import com.canplay.milk.mvp.activity.mine.WifiSettingActivity;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.HintDialogone;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.WaveViewCyc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;
import socket.bean.MilkConstant;
import socket.util.HexUtil;
import udpsocket.core.StaticPackage;
import udpsocket.core.TcpClientManager;
import udpsocket.core.UDPSocketBroadCast;

/**
 * 一键冲奶
 */
public class PushMilkActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.wave)
    WaveViewCyc wave;
    @BindView(R.id.iv_img)
    RelativeLayout ivImg;
    @BindView(R.id.tv_ml)
    TextView tvMl;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_mls)
    TextView tvMls;
    @BindView(R.id.tv_wd)
    TextView tvWd;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_begin)
    TextView tvBegin;

    private SetMilk milk;


    @Override
    public void initViews() {
        setContentView(R.layout.acitivity_push_milk);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        milk = SpUtil.getInstance().getMilk();
        String milknames = SpUtil.getInstance().getString("milknames");
        if(TextUtil.isNotEmpty(milknames)){
            tvName.setText(milknames);
        }
        if (milk != null) {
            tvState.setText(milk.consistence.equals("high") ? "高" : (milk.consistence.equals("middle") ? "中" : "低"));
            tvMls.setText(milk.waterQuantity+"ml");
            tvWd.setText(milk.waterTemperature+"℃");
            tvMl.setText(milk.waterQuantity);
            int pross = (int) ((Integer.valueOf(milk.waterQuantity) / 240.0) * 100);
            wave.setWaterProgress(pross);

        }
        dialogone = new HintDialogone(this, 4);
        dialogone.setBindClickListener(new HintDialogone.BindClickListener() {
            @Override
            public void clicks(int type, int right) {
                sta = 1;
                startTime();
                if (right == 1) {
                    MilkConstant.selectCommnt(2, "3666");
                    TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(), PushMilkActivity.this);
                    if (TextUtil.isNotEmpty(MilkConstant.HEAD)) {
                        startHear();
                    }
                } else {
                    isStop = true;
                }
            }
        });
//        mWindowAddPhoto = new PhotoPopupWindow(this);
    }

    private HintDialogone dialogone;

    @Override
    public void bindEvents() {

        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                Intent intent = new Intent(PushMilkActivity.this, AddMilkActivity.class);
                startActivityForResult(intent,3);
            }

            @Override
            public void navigationimg() {

            }
        });

        tvBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        TcpClientManager.HEART_TIME=500;



                        String a1=(Integer.toHexString(Integer.valueOf(milk.waterTemperature)) + "").toUpperCase();
                        String a2=(Integer.toHexString(Integer.valueOf(milk.waterQuantity)) + "").toUpperCase();
                        String a3= (milk.consistence.equals("high") ? "03" : (milk.consistence.equals("middle") ? "02" : "01"));
                        String a4= (changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + "";
                        String a5= (getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(milk.waterQuantity)))))) + "").toUpperCase();
//                        String content = (Integer.toHexString(Integer.valueOf(milk.waterTemperature)) + "").toUpperCase()  +(Integer.toHexString(Integer.valueOf(milk.waterQuantity)) + "").toUpperCase() + (milk.consistence.equals("high") ? "03" : (milk.consistence.equals("middle") ? "02" : "01")+(changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + ""
//                                +(getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(milk.waterQuantity)))))) + "")).toUpperCase();
                        String s = (a1+a2+a3+a4+a5);
                        MilkConstant.selectCommnt(3, s);
                        String cont = MilkConstant.sendCommend();
                        if(TextUtil.isEmpty(cont)){
                            startActivity(new Intent(PushMilkActivity.this, WifiSettingActivity.class));

                            return;
                        }
                        TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(), PushMilkActivity.this);








            }
        });
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

    private CountDownTimer countDownTimers;
    public void startHear() {
        countDownTimers = new CountDownTimer(500000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isStop){
                    if (TextUtil.isNotEmpty(MilkConstant.CONT)) {
                        isStop = true;
                        if (sta != 0 && MilkConstant.CONT.equals("入水温度过高")) {
                            type = 2;
                            conts = MilkConstant.CONT;
                            setType(2);
                            return;
                        }
                        type = 2;
                        conts = MilkConstant.CONT;
                        setType(2);
//                   mHandler.sendEmptyMessage(1);
                        countDownTimers.cancel();
                        return;
                    }
                    if (TextUtil.isNotEmpty(MilkConstant.SB)) {
                        if (MilkConstant.SB.equals("01")) {
                            if (MilkConstant.SEND == 0) {
                                if (sta != 0) {
                                    conts = "自清洗完成";
                                } else {
                                    conts = "泡奶成功";
                                }
                                type = 3;
//                           showToasts("泡奶成功");
//                           countDownTimers.cancel();
//                           mHandler.sendEmptyMessage(1);
                                setType(3);
//                           dimessProgress();
                                isStop = true;
                                cout = 0;
                            } else {
                                if (state == 2) {

                                    String a1=(Integer.toHexString(Integer.valueOf(milk.waterTemperature)) + "").toUpperCase();
                                    String a2=(Integer.toHexString(Integer.valueOf(milk.waterQuantity)) + "").toUpperCase();
                                    String a3= (milk.consistence.equals("high") ? "03" : (milk.consistence.equals("middle") ? "02" : "01"));
                                    String a4= (changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + "";
                                    String a5= (getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(milk.waterQuantity)))))) + "").toUpperCase();
//                                String content = (Integer.toHexString(Integer.valueOf(milk.waterTemperature)) + "").toUpperCase()  +(Integer.toHexString(Integer.valueOf(milk.waterQuantity)) + "").toUpperCase() + (milk.consistence.equals("high") ? "03" : (milk.consistence.equals("middle") ? "02" : "01")+(changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + ""
//                                        +(getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(milk.waterQuantity)))))) + "")).toUpperCase();
                                    String s = (a1+a2+a3+a4+a5);
//                                String content = Integer.toHexString(Integer.valueOf(milk.waterTemperature)) + ""  +Integer.toHexString(Integer.valueOf(milk.waterQuantity)) + "" + (milk.consistence.equals("高") ? "03" : (milk.consistence.equals("中") ? "02" : "01")+(changeInteger(SpUtil.getInstance().getWeight())<16?"0"+changeInteger(SpUtil.getInstance().getWeight()):Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getWeight())))) + ""
//                                        +getChange(Integer.toHexString(Integer.valueOf(changeInteger(SpUtil.getInstance().getMilkWeight(Integer.valueOf(milk.waterQuantity)))))) + "");
//                                String content = Integer.toHexString(Integer.valueOf(milk.waterTemperature)) + "" + Integer.toHexString(Integer.valueOf(milk.waterQuantity)) + "" + (milk.consistence.equals("高") ? "03" : (milk.consistence.equals("中") ? "02" : "01"));
                                    MilkConstant.selectCommnt(3, s);
                                    TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(), PushMilkActivity.this);




                                } else {
                                    if (cont == 0) {
                                        sta = 0;
                                        conts = "开始泡奶...";
                                        type = 1;
                                        setType(1);
//                                   mHandler.sendEmptyMessage(1);
//                                   showProgress("开始泡奶...");
                                        cont++;
                                    }

                                }

                                state = 0;
                            }

                        } else if (MilkConstant.SB.equals("02")) {

//                       if(cont==0){
//                           conts="设备需要清洗，开始清洗 ...";
                            if (sta == 0) {
                                type = 7;
                                setType(7);
//                           mHandler.sendEmptyMessage(1);
                            }

////                           showProgress("设备需要清洗，开始清洗 ...");
//                           cont++;
//                       }

//                       new Thread(new Runnable() {
//                           @Override
//                           public void run() {
//                               MilkConstant.selectCommnt(2,"3666");
//                               TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(),PushMilkActivity.this);
//                           }
//                       }).start();
                        } else if (MilkConstant.SB.equals("03")) {
                            if (cont == 0) {
                                conts = "设备清洗中...";
                                type = 1;
//                           mHandler.sendEmptyMessage(1);
                                setType(1);
//                           showProgress("设备清洗中...");
                                cont++;
                            }

                            state = 2;


                        } else if (MilkConstant.SB.equals("04")) {

                            sta = 0;
                            conts = "泡奶中...";
                            type = 1;
//                       mHandler.sendEmptyMessage(1);
//                           showProgress("泡奶中...")
                            setType(1);
// ;
                            cont++;


                        } else if (MilkConstant.SB.equals("05")) {
//                       dimessProgress();
                            conts = "正在冲水中请稍后";
                            type = 4;
                            setType(4);
//                       mHandler.sendEmptyMessage(1);
//                       showToasts("正在冲水中请稍后");
                            isStop = true;
                        }
                    }
                }



            }

            @Override
            public void onFinish() {


            }
        }.start();


    }

    public void setType(int type){
        if (type == 1) {
            if (TextUtil.isNotEmpty(conts)) {
                if (!isFinishing()) {
                    showProgress(conts);
                }

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
            dialogone.show(navigationBar);
        } else if (type == 3) {
            if (sta == 0) {
                BaseApplication.waterQuantity = milk.waterQuantity;

                RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.MILKS,milk.waterQuantity));
                dimessProgress();
                showToasts("泡奶成功");

                finish();


            } else {
                dimessProgress();
                showToasts(conts);
                finish();
            }

        } else {

            dimessProgress();
            if (TextUtil.isNotEmpty(conts)) {
                showToasts(conts);
            }

        }
    }
    public String conts;
    public int type;
    private boolean isSend;
    @Override
    public boolean handleMessage(Message msg) {

        return super.handleMessage(msg);
    }
   private boolean isStops=true;
    @Override
    protected void onDestroy() {
        BaseApplication.isReself=0;

        TcpClientManager.HEART_TIME=5000;
        if(dialogone!=null){
            dialogone.dismiss();
            dialogone=null;
        }
        super.onDestroy();
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

    private CountDownTimer countDownTimer1;

    /**
     * 是否完成
     */
    public boolean isStop;
    public Milk mi;
    public int cout;
    public int state;
    private int cont;

    private Subscription mSubscription;
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

    @Override
    public void initData() {
        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.WAIT == bean.type) {
                    String cont = (String) bean.content;
                    showToasts(cont);

                } else if (SubscriptionBean.OFF == bean.type) {
                    dimessProgress();
                } else  if (SubscriptionBean.MILK == bean.type) {
                    String cont= (String) bean.content;
                    if(!TextUtil.isEmpty(cont)){
                     if(cont.contains("开始泡奶")){
//                         mHandler.sendEmptyMessage(6);
                         type = 6;
                         setType(6);
                     }else {
                         showToasts(cont);
                     }
                    }else {
                        setType(6);
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
    }

    private int per;

    @Override
    protected void onResume() {
        milk = SpUtil.getInstance().getMilk();

        if (milk != null) {
            tvState.setText(milk.consistence.equals("high") ? "高" : (milk.consistence.equals("middle") ? "中" : "低"));
            tvMls.setText(milk.waterQuantity+"ml");
            tvWd.setText(milk.waterTemperature+"℃");
            tvMl.setText(milk.waterQuantity);
            int pross = (int) ((Integer.valueOf(milk.waterQuantity) / 240.0) * 100);
            wave.setWaterProgress(pross);
        }
//        if(TextUtil.isNotEmpty(MilkConstant.HEAD)){
//            TcpClientManager.getInstance().disConnect();
//            StaticPackage.selectPosition=0;
//            TcpClientManager.getInstance().startTcpClient();
//        }else {
//            starBrodcast();
//        }
        super.onResume();
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
    public <T> void toEntity(T entity, int type) {
//        showToasts("冲奶成功");
        finish();
    }

    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {
        showToasts(msg);
    }

    // 回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode==RESULT_OK){
                finish();
            }
    }
}
