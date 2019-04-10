package com.canplay.milk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.BaseFragment;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.HOME;
import com.canplay.milk.bean.ShareBean;
import com.canplay.milk.mvp.activity.MainActivity;
import com.canplay.milk.mvp.activity.WebViewWitesActivity;
import com.canplay.milk.mvp.activity.home.AddMilkActivity;
import com.canplay.milk.mvp.activity.home.MilkDetailActivity;
import com.canplay.milk.mvp.activity.home.OutWatherActivity;
import com.canplay.milk.mvp.activity.home.PushMilkActivity;
import com.canplay.milk.mvp.activity.home.RemindMilkActivity;
import com.canplay.milk.mvp.activity.mine.MineInfoActivity;
import com.canplay.milk.mvp.activity.mine.WifiSettingActivity;
import com.canplay.milk.mvp.activity.wiki.LookTImeActivity;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.ThirdShareManager;
import com.canplay.milk.util.TimeUtil;
import com.canplay.milk.view.EditorNameDialog;
import com.canplay.milk.view.HintDialogone;
import com.canplay.milk.view.PhotoPopupWindow;
import com.canplay.milk.view.PopView_NavigationBar_Menu;
import com.canplay.milk.view.SharePopupWindow;
import com.canplay.milk.view.WaveView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;
import socket.Decode.SvrCommnuManager;
import socket.bean.MilkConstant;
import socket.util.WifiUtils;
import udpsocket.core.StaticPackage;
import udpsocket.core.TcpClientManager;


/**
 * Created by mykar on 17/4/10.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener, BaseContract.View {

    @Inject
    BasesPresenter presenter;

    Unbinder unbinder;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.tv_mol)
    TextView tvMol;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.tv_zqx)
    TextView tvZqx;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.tv_milk)
    TextView tvMilk;
    @BindView(R.id.tv_change)
    TextView tvChange;
    @BindView(R.id.tv_ym)
    TextView tvYm;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_pop)
    ImageView ivPop;
    @BindView(R.id.ll_set)
    LinearLayout llSet;
    @BindView(R.id.ll_info)
    LinearLayout llInfo;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.ll_milk)
    LinearLayout llMilk;
    @BindView(R.id.wave)
    WaveView wave;
    @BindView(R.id.tv_title)
    TextView tvTitle;


    private EditorNameDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, null);
        unbinder = ButterKnife.bind(this, view);

        DaggerBaseComponent.builder().appComponent(((BaseApplication) getActivity().getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);


        mWindowAddPhoto = new PhotoPopupWindow(getActivity());
        sharePopupWindow = new SharePopupWindow(getActivity());
        sharePopupWindow.setSureListener(new SharePopupWindow.ClickListener() {
            @Override
            public void clickListener(int type) {
                if (type == 1) {
                    ThirdShareManager.getInstance().shareWeChat(new ShareBean(), true);
                } else {
                    ThirdShareManager.getInstance().shareWeChat(new ShareBean(), false);
                }
                sharePopupWindow.dismiss();
            }
        });
        mWindowAddPhoto.setCont("解除绑定", "取消");
        mWindowAddPhoto.setColor(R.color.red_pop, 0);
        tvDate.setText(TimeUtil.StringData(System.currentTimeMillis()));
        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.WAIT == bean.type) {
                   String cont= (String) bean.content;
                    showToast(cont);

                } else  if (SubscriptionBean.CLERS == bean.type) {
                    String cont= (String) bean.content;

                    if(!TextUtil.isEmpty(cont)){
                        if(cont.contains("设备正在清洗中...")){
                            handler.sendEmptyMessage(6);
                            type = 6;
                        }else {
                            showToast(cont);
                        }
                    }else {
                        handler.sendEmptyMessage(6);
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
        initPopView();
        initListener();
        return view;
    }
    private Subscription mSubscription;
    public PhotoPopupWindow mWindowAddPhoto;
    public SharePopupWindow sharePopupWindow;

    @Override
    public void onResume() {
        super.onResume();
        if(presenter!=null){
            presenter.getHomeInfo();
        }

    }


    private void initListener() {

        llSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddMilkActivity.class));
            }
        });
        llInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MilkDetailActivity.class));

            }
        });
        llTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LookTImeActivity.class));
            }
        });
        llMilk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), WebViewWitesActivity.class);
                intent.putExtra(WebViewWitesActivity.WEBTITLE,"百科详情");
                intent.putExtra("titles", TextUtil.isNotEmpty(home.article.title)?home.article.title:"今日百科");
                intent.putExtra("img", TextUtil.isNotEmpty(home.article.resoureKey)?home.article.resoureKey:"");
                intent.putExtra("contents", TextUtil.isNotEmpty(home.article.shortContent)?home.article.shortContent:"宝宝的呵护");
                intent.putExtra("state",1);

                intent.putExtra(WebViewWitesActivity.WEBURL,home.article.url);
                startActivity(intent);
            }
        });
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), MineInfoActivity.class));

            }
        });
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PushMilkActivity.class));

            }
        });
        tvZqx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                         TcpClientManager.HEART_TIME=500;

                        MilkConstant.selectCommnt(2,"3666");
                        String cont = MilkConstant.sendCommend();
                        if(TextUtil.isEmpty(cont)){
                            startActivity(new Intent(getActivity(), WifiSettingActivity.class));
                            return;
                        }
//                        handler.sendEmptyMessage(6);
//                        type=6;
                        TcpClientManager.getInstance().SendMessage(cont,getActivity());

//                        if(TextUtil.isNotEmpty(MilkConstant.HEAD)){
//                            TcpClientManager.getInstance().disConnect();
//                            StaticPackage.selectPosition=0;
//                            TcpClientManager.getInstance().startTcpClient();
//                        }



            }
        });
        ivPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popView_navigationBar.showPopView();
//                sharePopupWindow.showAsDropDown(line);
            }
        });

    }
    private Thread thread;
    private CountDownTimer countDownTimers;
    public void startHear() {
        countDownTimers = new CountDownTimer(500000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(!isStop){
                    if(BaseApplication.send==6){
                        isStop=true;
                    }

                    if (TextUtil.isNotEmpty(MilkConstant.CONT)) {
                        isStop = true;
                        if(MilkConstant.CONT.equals("入水温度过高")){
                            type = 2;
                            conts = MilkConstant.CONT;
                            handler.sendEmptyMessage(1);
                            return;
                        }
                        type = 2;
                        conts = MilkConstant.CONT;
                        handler.sendEmptyMessage(1);
                        return;
                    }
                    if(TextUtil.isNotEmpty(MilkConstant.SB)&&MilkConstant.SX.equals("01")){
                        if(MilkConstant.SB.equals("01")){

                            if(MilkConstant.SEND==0){
                                conts="清洗成功";
                                type=2;
                                handler.sendEmptyMessage(1);
//                        showToasts("冲水成功");
//                        dimessProgress();
                                isStop=true;
                                cout=0;
                            }else {


                                conts="开始清洗...";
                                type=1;
                                handler.sendEmptyMessage(1);
//                            showProgress("开始冲水...");


                                state=0;
                            }

                        }else if(MilkConstant.SB.equals("02")){
                            conts="开始清洗 ...";
                            type=1;
                            handler.sendEmptyMessage(1);
//

                        }else if(MilkConstant.SB.equals("03")){
                            conts="设备清洗中...";
                            type=1;
                            handler.sendEmptyMessage(1);
//                    showProgress("设备清洗中...");
                            state=2;


                        }
                    }

                    if(BaseApplication.send==3){
                        BaseApplication.send=0;
                        isStop=true;
                    }

                }


            }

            @Override
            public void onFinish() {


            }
        }.start();

    }

    private void initView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mSubscription!=null){
            mSubscription.unsubscribe();
        }
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
    public String conts;
    public int type;
    private Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {

            if(type==1){
                if (!getActivity().isFinishing()) {
                    showProgress(conts);
                }

            }else  if(type==2){
                dimessProgress();
                showToast(conts);
            }else  if(type==6){
                type=0;
                if(TextUtil.isNotEmpty(MilkConstant.HEAD)){
                    startHear();
                }
            }else {
                showToast(conts);
            }

            return false;
        }
    });
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
                if(BaseApplication.send==6){
                    TcpClientManager.HEART_TIME=5000;
                    isStop=true;
                }
                MilkConstant.selectCommnt(1,"");
                TcpClientManager.getInstance().SendMessage(MilkConstant.sendCommends(),getActivity());
                if (TextUtil.isNotEmpty(MilkConstant.CONT)) {
                    isStop = true;
                    if(MilkConstant.CONT.equals("入水温度过高")){
                        return;
                    }
                    type = 2;
                    conts = MilkConstant.CONT;
                    handler.sendEmptyMessage(1);
                    return;
                }
                if(TextUtil.isNotEmpty(MilkConstant.SB)&&MilkConstant.SX.equals("01")){
                    if(MilkConstant.SB.equals("01")){
                        if(MilkConstant.SEND==0){
                            conts="清洗成功";
                            type=2;
                            handler.sendEmptyMessage(1);
//                        showToasts("冲水成功");
//                        dimessProgress();
                            isStop=true;
                            cout=0;
                        }else {


                            conts="开始清洗...";
                            type=1;
                            handler.sendEmptyMessage(1);
//                            showProgress("开始冲水...");


                            state=0;
                        }

                    }else if(MilkConstant.SB.equals("02")){
                        conts="开始清洗 ...";
                        type=1;
                        handler.sendEmptyMessage(1);
//

                    }else if(MilkConstant.SB.equals("03")){
                        conts="设备清洗中...";
                        type=1;
                        handler.sendEmptyMessage(1);
//                    showProgress("设备清洗中...");
                        state=2;


                    }
                }

                if(BaseApplication.send==3){
                    BaseApplication.send=0;
                    isStop=true;
                }
                try {
                    Thread.sleep(1000);// 正常
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    /**
     * popwindow
     */
    private PopView_NavigationBar_Menu popView_navigationBar;
   private HintDialogone dialogone;
    private void initPopView() {

//        mWindowAddPhoto = new PhotoPopupWindow(getActivity());
        popView_navigationBar = new PopView_NavigationBar_Menu(getActivity(), 1);
        popView_navigationBar.setView(line);

        popView_navigationBar.setClickListener(new PopView_NavigationBar_Menu.ItemCliskListeners() {
            @Override
            public void clickListener(int poition) {
                switch (poition) {
                    case 0://冲奶提醒
                        startActivity(new Intent(getActivity(), RemindMilkActivity.class));
                        break;
                    case 1://WIFI设置
                        startActivity(new Intent(getActivity(), WifiSettingActivity.class));
                        break;
                    case 2://饮水设置
                        startActivity(new Intent(getActivity(), OutWatherActivity.class));
                        break;
                    case 3://跟换奶粉品牌
                        startActivity(new Intent(getActivity(), MilkDetailActivity.class));
                        break;
                }
                popView_navigationBar.dismiss();
            }

        });
    }
   private HOME home;
    @Override
    public <T> void toEntity(T entity, int type) {
         home = (HOME) entity;
        if (home != null) {
            if (TextUtil.isNotEmpty(home.milkInfo.name)) {
                tvMilk.setText(home.milkInfo.name);
            }
            if (TextUtil.isNotEmpty(home.article.title)) {
                tvTitle.setText(home.article.title);
            }    if (TextUtil.isNotEmpty(home.milkInfo.subName)) {
                tvDetail.setText(home.milkInfo.subName+"("+home.milkInfo.grade+"段)");
                SpUtil.getInstance().putString("milknames",home.milkInfo.subName+"("+home.milkInfo.grade+"段)");
            } else {
                tvDetail.setText("");
            }   if (TextUtil.isNotEmpty(home.todayMilk)) {
                tvMol.setText(home.todayMilk+"\nml");
            }
            tvTime.setText(TimeUtil.formatToMs(home.article.updateTime));
            SpUtil.getInstance().putLong("nextTime", home.nextTime);
            long nextTime = home.nextTime;
            long time = nextTime - System.currentTimeMillis();
            if (time > 0) {
                long day = time / (1000 * 3600 * 24);
                tvYm.setText(day+1 + "天");
                BaseApplication.day=day+"";
            }else {
                tvYm.setText("无");
            }
        }
    }

    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {

        showToast(msg);
    }
}
