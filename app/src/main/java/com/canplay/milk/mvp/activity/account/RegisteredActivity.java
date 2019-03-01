package com.canplay.milk.mvp.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.Rigter;
import com.canplay.milk.mvp.activity.WebViewWitesActivity;
import com.canplay.milk.mvp.activity.WebViewWitesActivitys;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.LoginContract;
import com.canplay.milk.mvp.present.LoginPresenter;
import com.canplay.milk.util.PwdCheckUtil;
import com.canplay.milk.util.StringUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.MCheckBox;
import com.canplay.milk.view.NavigationBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class RegisteredActivity extends BaseActivity implements LoginContract.View {

    @Inject
    LoginPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_phone)
    ClearEditText etPhone;
    @BindView(R.id.et_code)
    ClearEditText etCode;
    @BindView(R.id.tv_getcode)
    TextView tvGetcode;
    @BindView(R.id.et_pass)
    ClearEditText etPass;
    @BindView(R.id.et_user)
    ClearEditText etUser;
    @BindView(R.id.iv_choose)
    MCheckBox ivChoose;
    @BindView(R.id.tv_registered)
    TextView tvRegistered;
    @BindView(R.id.tv_next)
    TextView tvSave;
    @BindView(R.id.iv_gou)
    ImageView ivGou;
    private TimeCount timeCount;
    private String jobId;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_registered);
        ButterKnife.bind(this);
        navigationBar.setNavigationBarListener(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        //计时器
        timeCount = new TimeCount(60000, 1000);
    }

    @Override
    public void bindEvents() {

        tvGetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                if (TextUtil.isNotEmpty(phone) && phone.length() == 11) {
                    presenter.getCode(phone);

                } else {
                    showToasts("请输入正确手机号");
                }
            }
        });
        tvRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisteredActivity.this, WebViewWitesActivitys.class);
                intent.putExtra(WebViewWitesActivity.WEBTITLE,"注册服务协议");
                intent.putExtra(WebViewWitesActivity.WEBURL,"https://www.baidu.com");
                startActivity(intent);
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                if (TextUtil.isEmpty(phone)) {
                    showToasts("请输入手机号");
                    return;
                }
                if (phone.length() != 11) {
                    showToasts("请输入正确手机号");
                    return;
                }
                if (TextUtil.isEmpty(etCode.getText().toString())) {
                    showToasts("请输入验证码");
                    return;
                }
                if (TextUtil.isEmpty(etPass.getText().toString())) {
                    showToasts("请输入密码");
                    return;
                }
                if (TextUtil.isEmpty(etUser.getText().toString())) {
                    showToasts("请再次输入密码");
                    return;
                }
                if (!etPass.getText().toString().equals(etUser.getText().toString())) {
                    showToasts("两次密码不一致");
                    return;
                }
                if (!PwdCheckUtil.isLetterDigit(etPass.getText().toString())) {
                    showToasts("密码至少6位数且包含数字,字母");
                    return;
                }
                if (!ivChoose.isCheck()) {
                    showToasts("请同意注册协议");
                    return;
                }
                showProgress("注册中...");
                presenter.checkCode(etPhone.getText().toString().trim(), etCode.getText().toString(), StringUtil.md5(etPass.getText().toString().trim()));
                rigter.mobile = etPhone.getText().toString().trim();
                rigter.pwd = StringUtil.md5(etPass.getText().toString().trim());
                rigter.regCode = etCode.getText().toString().trim();

            }
        });

        etCode.setOnClearEditTextListener(new ClearEditText.ClearEditTextListener() {
            @Override
            public void afterTextChanged4ClearEdit(Editable s) {
                if (TextUtil.isNotEmpty(s.toString()) && s.toString().length() == 6) {
                    isSelect(true);
                } else {
                    isSelect(false);
                }
            }

            @Override
            public void changeText(CharSequence s) {

            }
        });
        etPhone.setOnClearEditTextListener(new ClearEditText.ClearEditTextListener() {
            @Override
            public void afterTextChanged4ClearEdit(Editable s) {
                if (s.toString().length() < 11) {
                    etCode.setText("");
                    tvGetcode.setText("获取验证码");
                    tvGetcode.setEnabled(true);
                    ivGou.setVisibility(View.GONE);
                }else {
                    if(TextUtil.isNotEmpty(s.toString().trim())&&s.toString().length()==11){
                        ivGou.setVisibility(View.VISIBLE);
                    }else {
                        ivGou.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void changeText(CharSequence s) {

            }
        });
    }

    private Rigter rigter = new Rigter();
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
        if (timeCount != null) {
            timeCount.cancel();
        }
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }


    public void isSelect(boolean choose) {
        if (choose) {
            tvSave.setEnabled(true);
            tvSave.setBackground(getResources().getDrawable(R.drawable.login_selector));

        } else {
//            tvSave.setEnabled(false);
        }

    }


    @Override
    public <T> void toEntity(T entity, int type) {
        dimessProgress();
        if (type == 1) {
            timeCount.start();
        } else {

            Intent intent = new Intent(this, RegisteredSecondActivity.class);
            intent.putExtra("data", rigter);
            startActivity(intent);
        }
    }

    @Override
    public void showTomast(String msg) {
        dimessProgress();
        showToasts(msg);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    //计时器
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tvGetcode.setEnabled(false);
            tvGetcode.setText(millisUntilFinished / 1000 + getString(R.string.schongxinhuoqu));
        }

        @Override
        public void onFinish() {

            tvGetcode.setText(R.string.chongxinhuoqu);
            tvGetcode.setBackground(getResources().getDrawable(R.drawable.login_selector));
            tvGetcode.setEnabled(true);
        }
    }
}
