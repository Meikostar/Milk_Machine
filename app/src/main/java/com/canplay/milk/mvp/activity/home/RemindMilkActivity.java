package com.canplay.milk.mvp.activity.home;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.BaseDailogManager;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.AlarmClock;
import com.canplay.milk.bean.Remind;
import com.canplay.milk.mvp.adapter.RemindMeasureAdapter;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.AudioPlayer;
import com.canplay.milk.util.MyUtil;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.TimeUtil;
import com.canplay.milk.view.MCheckBox;
import com.canplay.milk.view.MarkaBaseDialog;
import com.canplay.milk.view.NavigationBar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 冲奶提醒
 */
public class RemindMilkActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;

    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_remind)
    TextView tvRemind;

    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.iv_choose)
    MCheckBox ivChoose;
    @BindView(R.id.rl_menu)
    ListView rlMenu;

    private Map<Integer, String> map = new HashMap<>();

    @Override
    public void initViews() {
        setContentView(R.layout.activity_remind_health);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        navigationBar.setNavigationBarListener(this);
        type = getIntent().getIntExtra("type", 0);
        if (type != 0) {
            tvAdd.setVisibility(View.VISIBLE);
        }


    }


    private int status;

    @Override
    public void bindEvents() {

        ivChoose.setOnCheckClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.updateUserRemindStatus(ivChoose.isCheck()?"1":"0",ivChoose.isCheck());
            }
        });

        boolean isopen = SpUtil.getInstance().getBoolean("open", true);
        ivChoose.setChecked(isopen);
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {


            }

            @Override
            public void navigationimg() {
            }
        });
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(RemindMilkActivity.this, RemindSettingActivity.class), 0);

            }
        });

    }

    public void changeState(boolean isopen) {
        ivChoose.setChecked(!isopen);
        SpUtil.getInstance().putBoolean("open", !isopen);
        List<AlarmClock> allAlarm = SpUtil.getInstance().getAllAlarm();
        if(isopen){
            adapter.setData(allAlarm,1);
        }else {
            adapter.setData(allAlarm,0);
        }


    }
    private boolean isFirst=true;
    /**
     * 更新闹钟列表
     *
     * @param onOff 开关
     */
    private void updateTab(boolean onOff, AlarmClock alarmClock) {
        // 更新闹钟数据
//                        TabAlarmClockOperate.getInstance(mContext).update(onOff,
//                                alarmClock.getAlarmClockCode());
        alarmClock.setOnOff(onOff);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(alarmClock); //将对象转换成Json
        // 取得格式化后的时间
        String time = MyUtil.formatTime(alarmClock.getHour(),
                alarmClock.getMinute());
        SpUtil.getInstance().putString(time, jsonStr);
        if (onOff) {
            MyUtil.startAlarmClock(this, alarmClock);
        } else {
            // 关闭闹钟
            MyUtil.cancelAlarmClock(this,
                    Integer.valueOf(alarmClock.getId()));
            // 关闭小睡
            MyUtil.cancelAlarmClock(this,
                    -Integer.valueOf(alarmClock.getId()));
        }


    }

    private RemindMeasureAdapter adapter;

    @Override
    public void initData() {
        adapter = new RemindMeasureAdapter(this);
        rlMenu.setAdapter(adapter);

        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.MESURE == bean.type) {
                    setData();
                }else if (SubscriptionBean.WAIT == bean.type) {
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

        adapter.setListener(new RemindMeasureAdapter.selectItemListener() {
            @Override
            public void delete(AlarmClock medicine, int type, int poistion) {
                if (type == 6) {
                    alarmClock = medicine;
                    showDailog("删除" + medicine.getHour() + ":" + medicine.getMinute() + "闹铃");
                }else if (type == 1) {
                    Intent intent = new Intent(RemindMilkActivity.this, RemindSettingActivity.class);
                    intent.putExtra("data",medicine);
                    startActivity(intent);
                }else {
                    alarmClock = medicine;
                    if(type==2){
                        presenter.updateRemindStatus(medicine.getId(),1+"");

                    }else if(type==3){
                        presenter.updateRemindStatus(medicine.getId(),0+"");

                    }
                }

            }
        });
        setData();

    }


    private AlarmClock alarmClock;
    private MarkaBaseDialog dialog;

    public void showDailog(String content) {

        if (dialog != null) {
            dialog.cancel();
        }
        dialog = BaseDailogManager.getInstance().getBuilder(this).setRightButtonText("取消").setLeftButtonText("确定").setTitle("删除闹钟").setMessage(content).setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == BaseDailogManager.LEFT_BUTTON) {
                    showProgress("删除中...");
                    presenter.deleteRemind(alarmClock.getId());

                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        }).create();

        dialog.show();


    }

    private StringBuilder mRepeatStr = new StringBuilder();
    private Subscription mSubscription;
    private int hours = 24;
    private int minters = 60;
    private long totals;
    private int total=0;
    private int cout;
    public void setData() {
        totals=0;
        hours=24;
        minters=60;
        List<AlarmClock> allAlarm = SpUtil.getInstance().getAllAlarm();
        String time = TimeUtil.formatToMs(System.currentTimeMillis());
        String[] split = time.split(":");

        if (allAlarm != null && allAlarm.size() != 0) {
            for (AlarmClock alarmClock : allAlarm) {
                int hour = alarmClock.getHour() - Integer.valueOf(split[0]);
                int minter = alarmClock.getMinute() - Integer.valueOf(split[1]);

                if (alarmClock.isOnOff()) {
                    total=0;
                    if (hour >= 0) {
                        total=hour*3600;
                        if (minter>=0) {
                            total=total+minter*60;
                        }else {
                            if(hour==0){
                                total=(minter+60)*60+3600*23;
                            }else {
                                total=total+(minter+60)*60-3600;
                            }

                        }
                    } else  {
                        total=(hour+24)*3600;
                        if (minter>=0) {
                            total=total+minter*60;
                        }else {

                            total=total+(minter+60)*60-3600;
                        }
                    }

                }
                if(totals==0){
                    totals=total;
                    hours = (int) (totals / 3600);
                    minters= (int) ((totals% 3600)/60);


                }
                if(totals!=0&&totals>total){
                    totals=total;
                    hours = (int) (totals / 3600);
                    minters= (int) ((totals% 3600)/60);
                }
            }
        } else {
            if(cout==0){
                cout++;
                showProgress("加载中...");
                presenter.getReminds();
            }

        }


        if (minters == 60 && hours == 24) {
            tvRemind.setText("暂无提醒");
        } else {
            tvRemind.setText("下一次冲奶将会在" + (hours == 0 || hours == 24 ? "" : hours + "小时") + (minters == 0 || minters == 60 ? "" : minters + "分钟") + "后提醒");

        }

        adapter.setData(allAlarm, SpUtil.getInstance().getBoolean("open", true)?0:1);
    }

    @Override
    protected void onResume() {
        List<AlarmClock> allAlarm = SpUtil.getInstance().getAllAlarm();
        if(allAlarm.size()==0){
            if(presenter!=null){
                presenter.getReminds();
            }

        }

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    private Remind remind;
    private List<AlarmClock> allAlarm = new ArrayList<>();

    @Override
    public <T> void toEntity(T entity, int type) {
        dimessProgress();
        if(type==0){
            remind = (Remind) entity;
            allAlarm.clear();
            if (remind.userRemindStatus == 1) {
                SpUtil.getInstance().putBoolean("open", true);
                ivChoose.setChecked(true);
            } else {
                ivChoose.setChecked(false);
                SpUtil.getInstance().putBoolean("open", false);
            }
            if(remind!=null&&remind.list.size()!=0){

                for (Remind reminds : remind.list) {
                    AlarmClock mAlarmClock = BaseApplication.getInstance().mAlarmClock;
                    mAlarmClock.setOnOff((reminds.validStatus == 1) ? true : false);
                    mAlarmClock.setTag(reminds.name);
                    mAlarmClock.setId(reminds.id);
                    mAlarmClock.setWeeks(reminds.repeatDay);
                    String repat = "";
                    String[] split = reminds.repeatDay.split(",");
                    map.clear();
                    for (String value : split) {
                        Integer number = Integer.valueOf(value);
                        if (number == 1) {
                            map.put(1, "日");
                        } else if (number == 2) {
                            map.put(2, "一");
                        } else if (number == 3) {
                            map.put(3, "二");
                        } else if (number == 4) {
                            map.put(4, "三");
                        } else if (number == 5) {
                            map.put(5, "四");
                        } else if (number == 6) {
                            map.put(6, "五");
                        } else if (number == 7) {
                            map.put(7, "六");
                        }
                    }
                    mRepeatStr.setLength(0);
                    if (split.length == 7) {
                        mRepeatStr.append("每天");
                    } else if (split.length == 5 && reminds.repeatDay.contains("6") && reminds.repeatDay.contains("2") && reminds.repeatDay.contains("3") && reminds.repeatDay.contains("4")
                            && reminds.repeatDay.contains("5")) {
                        mRepeatStr.append("平日");
                    } else if (split.length == 5 && reminds.repeatDay.contains("1") && reminds.repeatDay.contains("7")) {
                        mRepeatStr.append("周末");

                    } else {
                        mRepeatStr.append(getString(R.string.week));
                        Collection<String> col = map.values();
                        for (String aCol : col) {
                            mRepeatStr.append(aCol).append(getResources().getString(R.string.caesura));
                        }
                        // 去掉最后一个"、"
                        mRepeatStr.setLength(mRepeatStr.length() - 1);
                        mAlarmClock.setRepeat(mRepeatStr.toString());
                    }
                    String[] split1 = reminds.remindTime.split(":");
                    mAlarmClock.setHour(Integer.valueOf(split1[0]));
                    mAlarmClock.setMinute(Integer.valueOf(split1[1]));
                    String time = MyUtil.formatTime(mAlarmClock.getHour(),
                            mAlarmClock.getMinute());
                    addList(mAlarmClock, time);
                    allAlarm.add(mAlarmClock);
                }
                adapter.setData(allAlarm,0);
                setData();
            }
        }else  if(type==1){
            updateTab(true,alarmClock);
        }else  if(type==2){
            updateTab(false,alarmClock);
            // 取消闹钟
            MyUtil.cancelAlarmClock(RemindMilkActivity.this,Integer.valueOf(alarmClock.getId()));
            // 取消小睡
            MyUtil.cancelAlarmClock(RemindMilkActivity.this,
                    -Integer.valueOf(alarmClock.getId()));

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(
                            Activity.NOTIFICATION_SERVICE);
            // 取消下拉列表通知消息
            notificationManager.cancel(Integer.valueOf(alarmClock.getId()));

            // 停止播放
            AudioPlayer.getInstance(RemindMilkActivity.this).stop();
        }else if(type==6){
            List<AlarmClock> allAlarm = SpUtil.getInstance().getAllAlarm();
            for (AlarmClock alarm : allAlarm) {
                if (alarmClock.getId().equals(alarm.getId())) {
                    // 取得格式化后的时间
                    String time = MyUtil.formatTime(alarm.getHour(),
                            alarmClock.getMinute());
                    SpUtil.getInstance().putString(time, "");
                    String times = SpUtil.getInstance().getString("time");
                    String data = "";
                    if (TextUtil.isNotEmpty(times)) {
                        String[] split1 = times.split(",");
                        for (int i = 0; i < split1.length; i++) {
                            if (!time.equals(split1[i])) {
                                if (TextUtil.isNotEmpty(data)) {
                                    data = data + "," + split1[i];
                                } else {
                                    data = split1[i];
                                }
                            }
                        }
                        SpUtil.getInstance().putString("time", data);
                    }
                    // 关闭闹钟
                    MyUtil.cancelAlarmClock(RemindMilkActivity.this,
                            Integer.valueOf(alarmClock.getId()));
                    // 关闭小睡
                    MyUtil.cancelAlarmClock(RemindMilkActivity.this,
                            -Integer.valueOf(alarmClock.getId()));

                    NotificationManager notificationManager = (NotificationManager) RemindMilkActivity.this
                            .getSystemService(Activity.NOTIFICATION_SERVICE);
                    // 取消下拉列表通知消息
                    notificationManager.cancel(Integer.valueOf(alarmClock.getId()));
                    setData();
                }
            }
        }else {
            changeState(type==8?true:false);
        }



    }

    private void addList(AlarmClock ac, String times) {


        Gson gson = new Gson();
        String jsonStr = gson.toJson(ac); //将对象转换成Json
        SpUtil.getInstance().putString(times, jsonStr);
        String time = SpUtil.getInstance().getString("time");
        if (TextUtil.isNotEmpty(time) && !time.contains(times)) {
            if (TextUtil.isNotEmpty(time)) {
                SpUtil.getInstance().putString("time", time + "," + times);
            }
        } else {
            SpUtil.getInstance().putString("time", times);
        }
        if(ac.isOnOff()){
            MyUtil.startAlarmClock(this, ac);
        }



    }
    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {
        dimessProgress();
        if(TextUtil.isNotEmpty(msg)){
            showToasts(msg);
        }

    }
}
