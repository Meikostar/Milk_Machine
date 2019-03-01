package com.canplay.milk.mvp.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.BuildConfig;
import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseDailogManager;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.mvp.activity.account.LoginActivity;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.StringUtil;
import com.canplay.milk.view.MarkaBaseDialog;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.ProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity  {


    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.ll_change_psw)
    LinearLayout llChangePsw;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.ll_phone)
    LinearLayout llPhone;
    @BindView(R.id.tv_verison)
    TextView tvVerison;
    @BindView(R.id.ll_version)
    LinearLayout llVersion;
    @BindView(R.id.tv_exit)
    TextView tvExit;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        navigationBar.setNavigationBarListener(this);


//        mWindowAddPhoto = new PhotoPopupWindow(this);
    }

    @Override
    public void bindEvents() {

        llChangePsw.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

          }
      });

        llPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        tvExit.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              SpUtil.getInstance().clearData();
              Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
              startActivity(intent);
              RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.FINISH,""));
             finish();
          }
      });
    }


    @Override
    public void initData() {

    }







}
