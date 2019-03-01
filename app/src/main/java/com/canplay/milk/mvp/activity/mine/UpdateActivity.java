package com.canplay.milk.mvp.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.canplay.medical.BuildConfig;
import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.BaseDailogManager;
import com.canplay.milk.bean.BASE;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.LoginContract;
import com.canplay.milk.mvp.present.LoginPresenter;
import com.canplay.milk.util.StringUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.MarkaBaseDialog;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.ProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 检查更新
 */
public class UpdateActivity extends BaseActivity  implements LoginContract.View{

    @Inject
    LoginPresenter presenter;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.ll_update)
    LinearLayout llUpdate;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_update);
        ButterKnife.bind(this);
        navigationBar.setNavigationBarListener(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        presenter.getLastestVersion();
//        mWindowAddPhoto = new PhotoPopupWindow(this);
    }

    @Override
    public void bindEvents() {
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(havaUpdate){
//                    new Thread(new DownloadApk(url)).start();
                }

            }
        });
    }


    @Override
    public void initData() {

    }



    private boolean havaUpdate;
    private BASE base;
    private String url;
    @Override
    public <T> void toEntity(T entity, int type) {
        base= (BASE) entity;
        if(TextUtil.isNotEmpty(base.version)){
            url=base.sdkUrlName;
            String newVersion = base.version;

            String oldVersion = StringUtil.getVersion(this);//"0.17"

            try {
                if (newVersion.compareTo(oldVersion) > 0) {
                    havaUpdate=true;
                    tvVersion.setText("已有新版本："+base.version+ "跟新后体验更多新功能");
                }else {
                    tvUpdate.setText("暂无更新");
                }

            } catch (Exception e) {
//            if (!StringUtil.equals(newVersion, oldVersion)) {
//                hasNew=true;
//            }else {
//                hasNew=false;
//            }
            }
        }
    }



    @Override
    public void showTomast(String msg) {
        dimessProgress();
         showToasts(msg);
    }
}
