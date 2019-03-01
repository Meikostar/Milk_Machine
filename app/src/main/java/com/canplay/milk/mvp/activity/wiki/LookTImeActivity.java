package com.canplay.milk.mvp.activity.wiki;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.BASE;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TimeUtil;
import com.canplay.milk.view.MCheckBox;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.TimeSelectorDialog;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 查看接种时间
 */
public class LookTImeActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;

    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.ll_my)
    LinearLayout llMy;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.tv_time2)
    TextView tvTime2;
    @BindView(R.id.ll_time2)
    LinearLayout llTime2;
    @BindView(R.id.tv_now)
    TextView tvNow;
    @BindView(R.id.ll_now)
    LinearLayout llNow;
    @BindView(R.id.tv_day)
    TextView tvDay;
    @BindView(R.id.iv_wifi)
    MCheckBox ivWifi;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.tv_times)
    TextView tvTimes;
    private TimeSelectorDialog selectorDialog;
    private String time;
    private BASE base;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_look_time);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        navigationBar.setNavigationBarListener(this);
        base = SpUtil.getInstance().getBASE();
        selectorDialog = new TimeSelectorDialog(LookTImeActivity.this);
        selectorDialog.setDate(new Date(System.currentTimeMillis()))
                .setBindClickListener(new TimeSelectorDialog.BindClickListener() {
                    @Override
                    public void time(String data, int poition, String times, String timess) {
                        time = data;
                        tvTime2.setText(timess);
                        presenter.setUserVaccineTime(base.id, data);
                    }

                });


    }

    @Override
    public void bindEvents() {
        ivWifi.setOnCheckClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivWifi.isCheck()) {
                    presenter.setUserVaccineRemindStatus(0 + "");
                } else {
                    presenter.setUserVaccineRemindStatus(1 + "");
                }
            }
        });
        llTime2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectorDialog.show(findViewById(R.id.line));
                selectorDialog.setTitle("修改实际接种时间");
            }
        });
//        tvTimes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectorDialog.show(findViewById(R.id.line));
//            }
//        });
//        tvTime2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectorDialog.show(findViewById(R.id.line));
//            }
//        });
    }


    @Override
    public void initData() {
        if (base != null) {
            if(base.name!=null&&base.frequency!=null){
                tvContent.setText(base.name + "(第" + base.frequency + "次,"+TimeUtil.formatTims(base.nextTime)+")");
            }

            tvTime.setText(TimeUtil.formatTims(base.previousTime));
            tvTime2.setText(TimeUtil.formatTims(base.nextTime));
            long nextTime = base.nextTime;
            long time = nextTime - System.currentTimeMillis();
            if (time > 0) {
                long day = time / (1000 * 3600 * 24)+1;
                tvDay.setText(day + "天");
            }
            int state = SpUtil.getInstance().getInt(SpUtil.ONPENSTATE);
            if (state==1) {
                ivWifi.setChecked(true);
            } else {
                ivWifi.setChecked(false);
            }
        }

        tvNow.setText(TimeUtil.formatTims(System.currentTimeMillis()));
    }


    @Override
    public <T> void toEntity(T entity, int type) {
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.REFESH_TIME, ""));
        if (type == 3) {
            showToasts("修改成功");
            if (ivWifi.isCheck()) {
                SpUtil.getInstance().putInt("validStatus",0);
                ivWifi.setChecked(false);
            } else {
                SpUtil.getInstance().putInt("validStatus",1);
                ivWifi.setChecked(true);
            }
        }else {
            long nextTime = TimeUtil.getStringToDate(time);
            long time = nextTime - System.currentTimeMillis();
            if (time > 0) {
                long day = time / (1000 * 3600 * 24)+1;
                BaseApplication.day=day+"";
                tvDay.setText(day + "天");
            }
        }

    }

    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {

    }


}
