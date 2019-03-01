package com.canplay.milk.mvp.activity.home;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.BaseDailogManager;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.AlarmClock;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.MyUtil;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.ToastUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.HourSelector;
import com.canplay.milk.view.MarkaBaseDialog;
import com.canplay.milk.view.NavigationBar;
import com.google.gson.Gson;
import com.google.zxing.client.android.decode.WeacConstants;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 冲奶设置
 */
public class RemindSettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener, BaseContract.View {

    @Inject
    BasesPresenter presenter;

    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tog_btn_monday)
    ToggleButton togBtnMonday;
    @BindView(R.id.tog_btn_tuesday)
    ToggleButton togBtnTuesday;
    @BindView(R.id.tog_btn_wednesday)
    ToggleButton togBtnWednesday;
    @BindView(R.id.tog_btn_thursday)
    ToggleButton togBtnThursday;
    @BindView(R.id.tog_btn_friday)
    ToggleButton togBtnFriday;
    @BindView(R.id.tog_btn_saturday)
    ToggleButton togBtnSaturday;
    @BindView(R.id.tog_btn_sunday)
    ToggleButton togBtnSunday;
    @BindView(R.id.tv_tag)
    TextView tvTag;
    @BindView(R.id.ll_tag)
    LinearLayout llTag;
    @BindView(R.id.hour_select)
    HourSelector hourSelect;
    @BindView(R.id.textView2)
    TextView textView2;
    private AlarmClock alarmClock;

    @Override
    public void initViews() {
        setContentView(R.layout.acitivity_remind_setting);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        alarmClock = getIntent().getParcelableExtra("data");
        mMap = new TreeMap<>();
        showDailog();
        togBtnMonday.setOnCheckedChangeListener(this);
        togBtnTuesday.setOnCheckedChangeListener(this);
        togBtnWednesday.setOnCheckedChangeListener(this);
        togBtnThursday.setOnCheckedChangeListener(this);
        togBtnFriday.setOnCheckedChangeListener(this);
        togBtnSaturday.setOnCheckedChangeListener(this);
        togBtnSunday.setOnCheckedChangeListener(this);

        mRepeatStr = new StringBuilder();
//        mWindowAddPhoto = new PhotoPopupWindow(this);
    }

    @Override
    public void bindEvents() {
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                if (!isMondayChecked && !isTuesdayChecked && !isWednesdayChecked
                        && !isThursdayChecked && !isFridayChecked && !isSaturdayChecked
                        && !isSundayChecked) {
                    ToastUtil.showToast("请选择提醒时间");
                    return;
                }
                if (TextUtil.isNotEmpty(tvTag.getText().toString())) {
                    mAlarmClock.setTag(tvTag.getText().toString());
                } else {
                    ToastUtil.showToast("请添加标题");
                    return;
                }
                mAlarmClock.setHour(Integer.valueOf(hourSelect.getHour()));
                mAlarmClock.setMinute(Integer.valueOf(hourSelect.getMinute()));

//                mAlarmClock.setId(SpUtil.getInstance().getAllAlarm().size()+"");
                if (alarmClock != null) {
                    showProgress("修改提醒中...");
                    presenter.updateRemind(mAlarmClock.getId() + "", mAlarmClock.getTag(), mAlarmClock.getHour() + ":" + mAlarmClock.getMinute(), mAlarmClock.getWeeks());
                } else {
                    String time = SpUtil.getInstance().getString("time");
                    if (time != null && time.contains(hourSelect.getSelector())) {
                        showToasts("该时间闹铃已存在");
                       return;
                    }

                    showProgress("保存提醒中...");
                    presenter.insertRemind(mAlarmClock.getTag(), mAlarmClock.getHour() + ":" + mAlarmClock.getMinute(), mAlarmClock.getWeeks());
                }


                // 取得格式化后的时间

            }

            @Override
            public void navigationimg() {

            }
        });
        llTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

    }

    private int hour;
    private int minter;
    /**
     * 周一按钮状态，默认未选中
     */
    private Boolean isMondayChecked = true;

    /**
     * 周二按钮状态，默认未选中
     */
    private Boolean isTuesdayChecked = true;

    /**
     * 周三按钮状态，默认未选中
     */
    private Boolean isWednesdayChecked = true;

    /**
     * 周四按钮状态，默认未选中
     */
    private Boolean isThursdayChecked = true;

    /**
     * 周五按钮状态，默认未选中
     */
    private Boolean isFridayChecked = true;

    /**
     * 周六按钮状态，默认未选中
     */
    private Boolean isSaturdayChecked = true;

    /**
     * 周日按钮状态，默认未选中
     */
    private Boolean isSundayChecked = true;

    /**
     * 保存重复描述信息String
     */
    private StringBuilder mRepeatStr;


    /**
     * 按键值顺序存放重复描述信息
     */
    private TreeMap<Integer, String> mMap;

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            // 选中周一
            case R.id.tog_btn_monday:
                if (isChecked) {
                    isMondayChecked = true;
                    mMap.put(1, getString(R.string.one_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isMondayChecked = false;
                    mMap.remove(1);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周二
            case R.id.tog_btn_tuesday:
                if (isChecked) {
                    isTuesdayChecked = true;
                    mMap.put(2, getString(R.string.two_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isTuesdayChecked = false;
                    mMap.remove(2);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周三
            case R.id.tog_btn_wednesday:
                if (isChecked) {
                    isWednesdayChecked = true;
                    mMap.put(3, getString(R.string.three_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isWednesdayChecked = false;
                    mMap.remove(3);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周四
            case R.id.tog_btn_thursday:
                if (isChecked) {
                    isThursdayChecked = true;
                    mMap.put(4, getString(R.string.four_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isThursdayChecked = false;
                    mMap.remove(4);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周五
            case R.id.tog_btn_friday:
                if (isChecked) {
                    isFridayChecked = true;
                    mMap.put(5, getString(R.string.five_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isFridayChecked = false;
                    mMap.remove(5);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周六
            case R.id.tog_btn_saturday:
                if (isChecked) {
                    isSaturdayChecked = true;
                    mMap.put(6, getString(R.string.six_h));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isSaturdayChecked = false;
                    mMap.remove(6);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;
            // 选中周日
            case R.id.tog_btn_sunday:
                if (isChecked) {
                    isSundayChecked = true;
                    mMap.put(7, getString(R.string.day));
                    setRepeatDescribe();
                    displayCountDown();
                } else {
                    isSundayChecked = false;
                    mMap.remove(7);
                    setRepeatDescribe();
                    displayCountDown();
                }
                break;

        }

    }

    private View view;
    private MarkaBaseDialog dialog;
    private TextView sure;
    private TextView cancel;
    private ClearEditText title;

    public void showDailog() {


        dialog = BaseDailogManager.getInstance().getBuilder(this).setLayoutID(R.layout.base_two_button_editor).setOnClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == BaseDailogManager.RIGHT_BUTTON) {
                    dialog.dismiss();

                } else {
                    dialog.dismiss();
                    tvTag.setText(MarkaBaseDialog.contents);
                }
            }
        }).create();


    }

    /**
     * 计算显示倒计时信息
     */
    private void displayCountDown() {
        // 取得下次响铃时间
        long nextTime = MyUtil.calculateNextTime(mAlarmClock.getHour(),
                mAlarmClock.getMinute(), mAlarmClock.getWeeks());
        // 系统时间
        long now = System.currentTimeMillis();
        // 距离下次响铃间隔毫秒数
        long ms = nextTime - now;

        // 单位秒
        int ss = 1000;
        // 单位分
        int mm = ss * 60;
        // 单位小时
        int hh = mm * 60;
        // 单位天
        int dd = hh * 24;

        // 不计算秒，故响铃间隔加一分钟
        ms += mm;
        // 剩余天数
        long remainDay = ms / dd;
        // 剩余小时
        long remainHour = (ms - remainDay * dd) / hh;
        // 剩余分钟
        long remainMinute = (ms - remainDay * dd - remainHour * hh) / mm;

        // 当剩余天数大于0时显示【X天X小时X分】格式
        if (remainDay > 0) {
            countDown = getString(R.string.countdown_day_hour_minute);
//            mTimePickerTv.setText(String.format(countDown, remainDay,
//                    remainHour, remainMinute));
            // 当剩余小时大于0时显示【X小时X分】格式
        } else if (remainHour > 0) {
            countDown = getResources()
                    .getString(R.string.countdown_hour_minute);
//            mTimePickerTv.setText(String.format(countDown, remainHour,
//                    remainMinute));
        } else {
            // 当剩余分钟不等于0时显示【X分钟】格式
            if (remainMinute != 0) {
                countDown = getString(R.string.countdown_minute);
//                mTimePickerTv.setText(String.format(countDown, remainMinute));
                // 当剩余分钟等于0时，显示【1天0小时0分】
            } else {
                countDown = getString(R.string.countdown_day_hour_minute);
//                mTimePickerTv.setText(String.format(countDown, 1, 0, 0));
            }

        }
    }

    /**
     * 响铃倒计时
     */
    private String countDown;

    /**
     * 设置重复描述的内容
     */
    private void setRepeatDescribe() {
        // 全部选中
        if (isMondayChecked & isTuesdayChecked & isWednesdayChecked
                & isThursdayChecked & isFridayChecked & isSaturdayChecked
                & isSundayChecked) {
            textView2.setText("提醒:" + getResources()
                    .getString(R.string.every_day));
            mAlarmClock.setRepeat(getString(R.string.every_day));
            // 响铃周期
            mAlarmClock.setWeeks("2,3,4,5,6,7,1");
            // 周一到周五全部选中
        } else if (isMondayChecked & isTuesdayChecked & isWednesdayChecked
                & isThursdayChecked & isFridayChecked & !isSaturdayChecked
                & !isSundayChecked) {
            textView2.setText("提醒:" + getString(R.string.week_day));
            mAlarmClock.setRepeat(getString(R.string.week_day));
            mAlarmClock.setWeeks("2,3,4,5,6");
            // 周六、日全部选中
        } else if (!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                & !isThursdayChecked & !isFridayChecked & isSaturdayChecked
                & isSundayChecked) {
            textView2.setText("提醒:" + getString(R.string.week_end));
            mAlarmClock.setRepeat(getString(R.string.week_end));
            mAlarmClock.setWeeks("7,1");
            // 没有选中任何一个
        } else if (!isMondayChecked & !isTuesdayChecked & !isWednesdayChecked
                & !isThursdayChecked & !isFridayChecked & !isSaturdayChecked
                & !isSundayChecked) {
            textView2.setText(getString(R.string.repeat_once));
            mAlarmClock.setRepeat(getResources()
                    .getString(R.string.repeat_once));
            mAlarmClock.setWeeks(null);

        } else {
            mRepeatStr.setLength(0);
            mRepeatStr.append(getString(R.string.week));
            Collection<String> col = mMap.values();
            for (String aCol : col) {
                mRepeatStr.append(aCol).append(getResources().getString(R.string.caesura));
            }
            // 去掉最后一个"、"
            mRepeatStr.setLength(mRepeatStr.length() - 1);
            textView2.setText("提醒:" + mRepeatStr.toString());
            mAlarmClock.setRepeat(mRepeatStr.toString());

            mRepeatStr.setLength(0);
            if (isMondayChecked) {
                mRepeatStr.append("2,");
            }
            if (isTuesdayChecked) {
                mRepeatStr.append("3,");
            }
            if (isWednesdayChecked) {
                mRepeatStr.append("4,");
            }
            if (isThursdayChecked) {
                mRepeatStr.append("5,");
            }
            if (isFridayChecked) {
                mRepeatStr.append("6,");
            }
            if (isSaturdayChecked) {
                mRepeatStr.append("7,");
            }
            if (isSundayChecked) {
                mRepeatStr.append("1,");
            }
            mAlarmClock.setWeeks(mRepeatStr.toString());
        }

    }


    @Override
    public void initData() {
        if (alarmClock == null) {
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
            togBtnMonday.setChecked(true);
            togBtnTuesday.setChecked(true);
            togBtnWednesday.setChecked(true);
            togBtnThursday.setChecked(true);
            togBtnFriday.setChecked(true);
            togBtnSaturday.setChecked(true);
            togBtnSunday.setChecked(true);
        } else {
            isMondayChecked = false;
            isTuesdayChecked = false;
            isWednesdayChecked = false;
            isThursdayChecked = false;
            isFridayChecked = false;
            isSaturdayChecked = false;
            isSundayChecked = false;
            hour = alarmClock.getHour();
            minter = alarmClock.getMinute();
            mAlarmClock = alarmClock;

            String[] split = mAlarmClock.getWeeks().split(",");
            if (TextUtil.isNotEmpty(mAlarmClock.getTag())) {
                tvTag.setText(mAlarmClock.getTag());
            }
            if (TextUtil.isNotEmpty(mAlarmClock.getRepeat())) {
                textView2.setText(mAlarmClock.getRepeat());
            }
            hourSelect.setData(mAlarmClock.getHour(), mAlarmClock.getMinute());
            for (String value : split) {
                Integer number = Integer.valueOf(value);
                if (number == 2) {
                    isMondayChecked=true;
                    togBtnMonday.setChecked(true);
                } else if (number == 3) {
                    isTuesdayChecked=true;
                    togBtnTuesday.setChecked(true);
                } else if (number == 4) {
                    isWednesdayChecked=true;
                    togBtnWednesday.setChecked(true);
                } else if (number == 5) {
                    isThursdayChecked=true;
                    togBtnThursday.setChecked(true);
                } else if (number == 6) {
                    isFridayChecked=true;
                    togBtnFriday.setChecked(true);
                } else if (number == 7) {
                    isSaturdayChecked=true;
                    togBtnSaturday.setChecked(true);
                } else if (number == 1) {
                    isSundayChecked=true;
                    togBtnSunday.setChecked(true);
                }
            }
        }

    }

    /**
     * 闹钟实例
     */
    private AlarmClock mAlarmClock;
    /**
     * 保存闹钟信息的list
     */
    private List<AlarmClock> mAlarmClockList;

    private void addList(AlarmClock ac, String times) {

        if (alarmClock != null) {
            List<AlarmClock> allAlarm = SpUtil.getInstance().getAllAlarm();
            for (AlarmClock alarm : allAlarm) {
                if (alarmClock.getId().equals(alarm.getId())) {


                    // 关闭闹钟
                    MyUtil.cancelAlarmClock(RemindSettingActivity.this,
                            Integer.valueOf(alarmClock.getId()));


                    NotificationManager notificationManager = (NotificationManager) RemindSettingActivity.this
                            .getSystemService(Activity.NOTIFICATION_SERVICE);
                    // 取消下拉列表通知消息
                    notificationManager.cancel(Integer.valueOf(alarmClock.getId()));

                }
            }
        }
        Gson gson = new Gson();
        String jsonStr = gson.toJson(ac); //将对象转换成Json
        SpUtil.getInstance().putString(times, jsonStr);
        String time = SpUtil.getInstance().getString("time");
        if (time != null && !time.contains(times)) {
            if (TextUtil.isNotEmpty(time)) {
                SpUtil.getInstance().putString("time", time + "," + times);
            }
        } else {
            if(time==null){
                SpUtil.getInstance().putString("time", times);
            }

        }
        boolean isopen = SpUtil.getInstance().getBoolean("open", true);

        if (isopen) {
            MyUtil.startAlarmClock(this, ac);
        }

        showToasts("添加成功");
        dimessProgress();

        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.MESURE, ""));
        finish();

    }


    @Override
    public <T> void toEntity(T entity, int type) {
        String id = (String) entity;
        if (TextUtil.isNotEmpty(id)) {
            mAlarmClock.setId(id);
        }
        if(alarmClock!=null){
            if(!(hour==Integer.valueOf(hourSelect.getHour())&&minter==Integer.valueOf(hourSelect.getMinute()))){
                delAram();
            }
        }

        addList(mAlarmClock, hourSelect.getSelector());

    }

    public void delAram(){
        List<AlarmClock> allAlarm = SpUtil.getInstance().getAllAlarm();
        for (AlarmClock alarm : allAlarm) {
            if (alarmClock.getId().equals(alarm.getId())) {
                // 取得格式化后的时间
                String time = MyUtil.formatTime(alarm.getHour(),
                        alarm.getMinute());
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
                MyUtil.cancelAlarmClock(RemindSettingActivity.this,
                        Integer.valueOf(alarmClock.getId()));
                // 关闭小睡
                MyUtil.cancelAlarmClock(RemindSettingActivity.this,
                        -Integer.valueOf(alarmClock.getId()));

                NotificationManager notificationManager = (NotificationManager) RemindSettingActivity.this
                        .getSystemService(Activity.NOTIFICATION_SERVICE);
                // 取消下拉列表通知消息
                notificationManager.cancel(Integer.valueOf(alarmClock.getId()));

            }
        }
    }
    @Override
    public void toNextStep(int type) {
        dimessProgress();

    }

    @Override
    public void showTomast(String msg) {
        showToasts(msg);
        dimessProgress();

    }
}
