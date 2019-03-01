package com.canplay.milk.mvp.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.Rigter;
import com.canplay.milk.bean.ShareBean;
import com.canplay.milk.mvp.activity.MainActivity;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.LoginContract;
import com.canplay.milk.mvp.present.LoginPresenter;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.ThirdShareManager;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.PhotoPopupWindow;
import com.canplay.milk.view.SharePopupWindow;
import com.canplay.milk.view.TimeSelectorDialog;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;

/**
 * 注册2
 */
public class RegisteredSecondActivity extends BaseActivity implements LoginContract.View{

    @Inject
    LoginPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_name)
    ClearEditText etName;
    @BindView(R.id.tv_choose)
    TextView tvChoose;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.ll_sex)
    LinearLayout llSex;
    @BindView(R.id.et_date)
    TextView etDate;
    @BindView(R.id.et_weight)
    ClearEditText etWeight;
    @BindView(R.id.tv_registered)
    TextView tvRegistered;
    @BindView(R.id.tv_next)
    TextView tvNext;
    private Subscription mSubscription;
    private Rigter rigter;
    private LinearLayoutManager mLinearLayoutManager;
    private int type;
    private boolean is_time;
    private boolean is_right;

    private String jobId;
    private String name;
    private String date;
    private String psw;

    private TimeSelectorDialog selectorDialog;
    private PhotoPopupWindow mWindowAddPhoto;
    @Override
    public void initViews() {
        setContentView(R.layout.activity_registered2);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        navigationBar.setNavigationBarListener(this);
        rigter= (Rigter) getIntent().getSerializableExtra("data");
        navigationBar.setNavigationBarListener(this);
        selectorDialog = new TimeSelectorDialog(RegisteredSecondActivity.this);
        selectorDialog.setDate(new Date(System.currentTimeMillis()))
                .setBindClickListener(new TimeSelectorDialog.BindClickListener() {
                    @Override
                    public void time(String time, int poition, String times,String timess) {
                        if (TextUtil.isNotEmpty(time)) {
                            etDate.setText(time);

                        }
                    }


                });
        mWindowAddPhoto = new PhotoPopupWindow(this);
        mWindowAddPhoto.setSureListener(new PhotoPopupWindow.ClickListener() {
            @Override
            public void clickListener(int type) {
                if(type==1){
                    tvSex.setText("女");
                }else {
                    tvSex.setText("男");
                }
            }
        });

    }

    @Override
    public void bindEvents() {
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
              selectorDialog.show(tvSex);
            }
        });
        llSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyBoard();
                mWindowAddPhoto.showAsDropDown(etDate);
            }
        });
        etName.setOnClearEditTextListener(new ClearEditText.ClearEditTextListener() {
            @Override
            public void afterTextChanged4ClearEdit(Editable s) {


            }

            @Override
            public void changeText(CharSequence s) {

            }
        });
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtil.isEmpty(etName.getText().toString())){
                    showToasts("请填写baby名称");
                    return;
                } if(TextUtil.isEmpty(tvSex.getText().toString())){
                    showToasts("请选择baby姓别");
                    return;
                }
                if(TextUtil.isEmpty(etDate.getText().toString())){
                    showToasts("请选择出生日期");
                    return;
                }  if(TextUtil.isEmpty(etWeight.getText().toString())){
                    showToasts("请填写baby体重");
                    return;
                }
                  showProgress("注册中...");
                presenter.mobileRegister(rigter.mobile,rigter.regCode,rigter.pwd,etDate.getText().toString(),
                        etName.getText().toString(),tvSex.getText().toString().equals("男")?"1":"0",etWeight.getText().toString());
            }
        });


    }


    @Override
    public void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    @Override
    public <T> void toEntity(T entity, int type) {
        dimessProgress();

        startActivity(new Intent(this, MainActivity.class));
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.FINISH,""));
        finish();
    }

    @Override
    public void showTomast(String msg) {
        dimessProgress();

        showToasts(msg);
    }
}
