package com.canplay.milk.mvp.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.DATA;
import com.canplay.milk.bean.SetMilk;
import com.canplay.milk.bean.Wather;
import com.canplay.milk.mvp.activity.mine.WifiSettingActivity;
import com.canplay.milk.mvp.adapter.Waterdapter;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.DensityUtil;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.HintDialogone;
import com.canplay.milk.view.NavigationBar;

import java.util.ArrayList;
import java.util.List;

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
 * 饮水设置
 */
public class OutWatherActivity extends BaseActivity implements BaseContract.View, View.OnTouchListener {

    @Inject
    BasesPresenter presenter;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.section_1)
    ProgressBar section1;

    @BindView(R.id.tv_milk)
    TextView tvMilk;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.ll_bg)
    LinearLayout llBg;


    private Waterdapter adater;
    private boolean isShow=true;
    private Animation animationhide;
    private Animation animationshow;
    @Override
    public void initViews() {
        setContentView(R.layout.activity_out_wahter);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        adater = new Waterdapter(this);
        listview.setAdapter(adater);
        animationhide = AnimationUtils.loadAnimation(this, R.anim.list_hide);
        animationshow = AnimationUtils.loadAnimation(this, R.anim.list_show);
        dialogone = new HintDialogone(this, 4);
        dialogone.setBindClickListener(new HintDialogone.BindClickListener() {
            @Override
            public void clicks(int type, int right) {
                sta=1;
                startTime();
                if(right==1){
                    MilkConstant.selectCommnt(2,"3666");
                    TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(),OutWatherActivity.this);
                    startHear();
                }else {
                    isStop=true;
                }
            }
        });
//        mWindowAddPhoto = new PhotoPopupWindow(this);
    }
    private HintDialogone dialogone;

    private Wather wather = new Wather();
    private float ml=60;
    private Subscription mSubscription;
    @Override
    public void bindEvents() {
        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.OFF == bean.type) {
                    dimessProgress();
                } else   if (SubscriptionBean.WAIT == bean.type) {
                    String cont= (String) bean.content;
                    showToasts(cont);

                }else  if (SubscriptionBean.WATER == bean.type) {

                    String cont= (String) bean.content;
                    if(!TextUtil.isEmpty(cont)){
                        if(cont.contains("开始冲水")){
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
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                if(ml!=60&&select==0){

                }else {
                    ml= (select)*30+60;
                }

                List<DATA> data = adater.getData();
                for(DATA data1:data){
                    if(data1.isCheck){
                        wd=data1.content;
                    }
                }
                wather.wd=Integer.valueOf(wd);
                wather.ml=ml;
                SpUtil.getInstance().putWahter(wather);
                showToasts("保存成功");
                finish();
            }

            @Override
            public void navigationimg() {

            }
        });


        section1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int X = (int) event.getRawX();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        if (X < DensityUtil.dip2px(40)) {
                            X = DensityUtil.dip2px(40);
                        } else if (X > width - DensityUtil.dip2px(40)) {
                            X = width - DensityUtil.dip2px(40);
                        } else {
                            X = X - DensityUtil.dip2px(20);
                        }
                        poition= getSelction(X);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(80));

                        layoutParams.topMargin = DensityUtil.dip2px(7);
                        layoutParams.leftMargin = poition-DensityUtil.dip2px(16);
                        layoutParams.width = DensityUtil.dip2px(40);
                        layoutParams.height = DensityUtil.dip2px(40);
                        ivImg.setLayoutParams(layoutParams);
                        poition=X;
                        break;

                }
                return true;

            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    isShow = false;
//                    listview.startAnimation(animationhide);
                    listview.setFocusable(false);
                    listview.setVisibility(View.GONE);
                    img.setImageDrawable(getResources().getDrawable(R.drawable.wather1));
                } else {
                    isShow = true;
//                    listview.startAnimation(animationshow);
                    listview.setFocusable(true);
                    listview.setVisibility(View.VISIBLE);
                    img.setImageDrawable(getResources().getDrawable(R.drawable.wather2));
                }
            }
        });


        tvMilk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ml!=0&&select==0){
                   if(ss!=0){
                       ml=60;
                   }
                }else {
                    ml= (select)*30+60;
                }

                List<DATA> data = adater.getData();
                for(DATA data1:data){
                    if(data1.isCheck){
                        wd=data1.content;
                    }
                }
                if(TextUtil.isEmpty(wd)||ml==0){
                    showToasts("请选择出水量和温度");
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String content = Integer.toHexString(Integer.valueOf(wd))+""+Integer.toHexString((int) ml);
                        MilkConstant.selectCommnt(4,content.toUpperCase());
                        String cont = MilkConstant.sendCommend();
                        if(TextUtil.isEmpty(cont)){
                           startActivity(new Intent(OutWatherActivity.this, WifiSettingActivity.class));
                            return;
                        }
//                        mHandler.sendEmptyMessage(6);
//                        type=6;


                        Looper.prepare();
                        TcpClientManager.getInstance().SendMessage(cont,OutWatherActivity.this);
                        Looper.loop();// 进入loop中的循环，查看消息队列

                    }
                }).start();

                wather.wd=Integer.valueOf(wd);
                wather.ml=ml;
                SpUtil.getInstance().putWahter(wather);

            }
        });
    }
    private CountDownTimer countDownTimer2;
    private int sta=0;
    public void startTime(){
        countDownTimer2 = new CountDownTimer(50000, 10000) {
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
    public String conts;
    public int type;
    @Override
    public boolean handleMessage(Message msg) {

        if(type==1){
            if (!isFinishing()) {
                showProgress(conts);
            }
        }else  if(type==2){
            dimessProgress();
            showToasts(conts);

        }else  if(type==6){
            type=0;
            startHear();

        }else  if(type==7){
            type=0;
            isStop=true;
            dialogone.show(line);
        }else {
            showToasts(conts);
        }
        return super.handleMessage(msg);
    }

    private Thread thread;
    public void startHear(){
        if(thread==null){
            isStop=false;
            thread= new Thread(runHeartbeat);
            thread.start();
        }else {
            thread=null;
            if(isStop){
                isStop=false;
                thread= new Thread(runHeartbeat);
                thread.start();
            }

        }

    }
    /**
     * 是否完成
     */
    public boolean isStop;
    public int cout;
    public int state;
    private Runnable runHeartbeat = new Runnable() {
        @Override
        public void run() {
            while (!isStop) {

                if(TextUtil.isNotEmpty(MilkConstant.CONT)){
                    if(sta!=0&&MilkConstant.CONT.equals("入水温度过高")){
                        return;
                    }
                    isStop=true;
                    type=2;
                    conts=MilkConstant.CONT;
                    mHandler.sendEmptyMessage(1);
                    return;
                }
               if(TextUtil.isNotEmpty(MilkConstant.SB)){
                   if(MilkConstant.SB.equals("01")){
                       if(MilkConstant.SEND==0){

                               if(sta!=0){
                                   conts="自清洗完成";
                               }else {
                                   conts="冲水成功";
                               }



                           type=2;
                           mHandler.sendEmptyMessage(1);
//                        showToasts("冲水成功");
//                        dimessProgress();
                           isStop=true;
                           cout=0;
                       }else {

                           if(state==2){
                               String content = Integer.toHexString(Integer.valueOf(wd))+""+Integer.toHexString((int) ml);
                               MilkConstant.selectCommnt(4,content.toUpperCase());
                               TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommend(),OutWatherActivity.this);
                           }else {
                               sta=0;
                               conts="开始冲水...";
                               type=1;
                               mHandler.sendEmptyMessage(1);
//                            showProgress("开始冲水...");
                           }

                           state=0;
                       }

                   }else if(MilkConstant.SB.equals("02")){

//                       conts="设备需要清洗，开始清洗 ...";
                       if(sta==0){
                           type=7;

////                    showProgress("设备需要清洗，开始清洗 ...");
                           mHandler.sendEmptyMessage(1);
                       }

                   }else if(MilkConstant.SB.equals("03")){
                       conts="设备清洗中...";
                       type=1;
                       mHandler.sendEmptyMessage(1);
//                    showProgress("设备清洗中...");
                       state=2;


                   }else if(MilkConstant.SB.equals("04")){
//                    dimessProgress();
                       conts="正在泡奶中请稍后";
                       type=2;
                       mHandler.sendEmptyMessage(1);
//                    showToasts("正在泡奶中请稍后");
                       isStop=true;
                   }else if(MilkConstant.SB.equals("05")){
                       sta=0;
                       conts="冲水中...";
                       type=1;
                       mHandler.sendEmptyMessage(1);
//                    showToasts("冲水中...");
                       state=2;

                   }
               }

                try {
                    Thread.sleep(800);// 正常
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                MilkConstant.selectCommnt(1,"");
                TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommends(),OutWatherActivity.this);
            }
        }
    };

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
   private int poition;
   private String wd="40";
    private int [] wds={60,55,50,46,43,40};
    @Override
    public void initData() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(80));

        layoutParams.topMargin = DensityUtil.dip2px(7);
        layoutParams.width = DensityUtil.dip2px(40);
        layoutParams.height = DensityUtil.dip2px(40);
        ivImg.setLayoutParams(layoutParams);
        ivImg.setOnTouchListener(this);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;         // 屏幕宽度（像素）
        per=(width-DensityUtil.dip2px(86))/6;
        Wather wahter = SpUtil.getInstance().getWahter();
        if(wahter!=null&&wahter.wd!=0){
            ml= wahter.ml;
            int X= getPoitions(ml);
            RelativeLayout.LayoutParams layoutParam= new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(80));
            poition=X;
            layoutParam.leftMargin = X;
            layoutParam.topMargin = DensityUtil.dip2px(7);
            layoutParam.width = DensityUtil.dip2px(40);
            layoutParam.height = DensityUtil.dip2px(40);

            ivImg.setLayoutParams(layoutParam);
        }
        for(int i=0;i<wds.length;i++){

            DATA dat = new DATA();
            dat.content=wds[i]+"";
            if(wahter!=null){
                if(wds[i]==wahter.wd){
                    dat.isCheck=true;
                }else {

                    dat.isCheck=false;
                }
            }else {
                if(i==70){
                    dat.isCheck=true;
                }else {

                    dat.isCheck=false;
                }
            }

            data.add(dat);
        }
        adater.setData(data);
    }
    private List<DATA> data=new ArrayList<>();
    private int width;
    private int select=0;
    public int getPoition(float poi){
        int i = (int) (poi / 60);
        return DensityUtil.dip2px(23)+(i-1)*per;
    }

    public int getPoitions(float po){
        float cout = (po - 60) / 30;
        return (int) (DensityUtil.dip2px(6)+cout*per);
    }
    public int getSelction(float poition){
        float len = (poition - DensityUtil.dip2px(23) )% per;
        int cout = (int) ((poition - DensityUtil.dip2px(23)) / per);
        float lend = len / per;
        if(lend>0.5){
            select=cout+1<7?cout+1:6;
            return select*per+DensityUtil.dip2px(23);
        }else {
            if(cout==0){
                ss=1;
            }
            select=cout<7?cout:6;
            return select*per+DensityUtil.dip2px(23);
        }

    }
   private  int ss=0;
    @Override
    public <T> void toEntity(T entity, int type) {

    }

    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {
        dimessProgress();
        showToasts(msg);
    }

   private int per;
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                _xDelta = X;
                _yDelta = DensityUtil.dip2px(7);
                break;
            case MotionEvent.ACTION_UP:
                 poition= getSelction(X);

                layoutParams = (RelativeLayout.LayoutParams) view
                        .getLayoutParams();
                layoutParams.leftMargin = poition-DensityUtil.dip2px(16);
                layoutParams.topMargin = DensityUtil.dip2px(7);
                layoutParams.width = DensityUtil.dip2px(40);
                layoutParams.height = DensityUtil.dip2px(40);
                view.setLayoutParams(layoutParams);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                if (width - DensityUtil.dip2px(40) > X && X > DensityUtil.dip2px(40)) {
                     layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - DensityUtil.dip2px(20);
                    layoutParams.topMargin = DensityUtil.dip2px(7);
                    layoutParams.width = DensityUtil.dip2px(40);
                    layoutParams.height = DensityUtil.dip2px(40);
                    view.setLayoutParams(layoutParams);
                }

                break;
        }

        ivImg.invalidate();
        return true;
    }

    private RelativeLayout.LayoutParams layoutParams;
    private int _xDelta;
    private int _yDelta;


}
