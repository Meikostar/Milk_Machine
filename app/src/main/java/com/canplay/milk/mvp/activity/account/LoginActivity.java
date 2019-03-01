package com.canplay.milk.mvp.activity.account;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.USER;
import com.canplay.milk.mvp.activity.MainActivity;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.LoginContract;
import com.canplay.milk.mvp.present.LoginPresenter;
import com.canplay.milk.permission.PermissionConst;
import com.canplay.milk.permission.PermissionFail;
import com.canplay.milk.permission.PermissionGen;
import com.canplay.milk.permission.PermissionSuccess;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.ClearEditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;


public class LoginActivity extends BaseActivity implements LoginContract.View {

    @Inject
    LoginPresenter presenter;
    @BindView(R.id.tv_logo)
    ImageView tvLogo;
    @BindView(R.id.et_user)
    ClearEditText etUser;
    @BindView(R.id.et_pws)
    ClearEditText etPws;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.tv_forget)
    TextView tvForget;
    @BindView(R.id.tv_registered)
    TextView tvRegistered;
    @BindView(R.id.tv_close)
    TextView tvClose;

    private int type;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        type = getIntent().getIntExtra("type", 0);
        PermissionGen.with(LoginActivity.this)
                .addRequestCode(PermissionConst.REQUECT_DATE)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.CHANGE_WIFI_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
        String userId = SpUtil.getInstance().getUserId();
        if (TextUtil.isNotEmpty(userId)) {
            if(type!=0){
                return;
            }
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @PermissionSuccess(requestCode = PermissionConst.REQUECT_DATE)
    public void requestSdcardSuccess() {

    }

    @PermissionFail(requestCode = PermissionConst.REQUECT_DATE)
    public void requestSdcardFailed() {
        showToasts("获取权限失败");
    }

    private int REQUEST_CODE_SCAN = 6;

    @Override
    public void bindEvents() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this,MainActivity.class));

                String user = etUser.getText().toString();
                String password = etPws.getText().toString();

                if (TextUtil.isEmpty(user)) {
                    showToasts(getString(R.string.qingshurusjh));
                    return;
                }
                if (TextUtil.isEmpty(password)) {
                    showToasts(getString(R.string.mimanull));
                    return;
                }
                presenter.goLogin(user, password);
                showProgress("登录中...");
            }
        });


        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//忘记密码
                finish();
            }
        });

        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//忘记密码
                Intent intent = new Intent(LoginActivity.this, ForgetFirstActivity.class);
                startActivity(intent);
            }
        });

        tvRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//注册
                Intent intent = new Intent(LoginActivity.this, RegisteredActivity.class);
                startActivity(intent);
            }
        });
    }

    private Subscription mSubscription;

    @Override
    public void initData() {

        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.FINISH == bean.type) {
                    finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    private USER user;

    @Override
    public <T> void toEntity(T entity, int types) {
        dimessProgress();
        if (types == 0) {
            user = (USER) entity;
            if (type != 0) {
                presenter.logout();
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else {
            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.FINISH, ""));
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


    }

    @Override
    public void showTomast(String msg) {
        dimessProgress();
        showToasts(msg);
    }

}
