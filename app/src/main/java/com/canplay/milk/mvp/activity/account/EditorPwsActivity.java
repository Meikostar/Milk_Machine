package com.canplay.milk.mvp.activity.account;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.LoginContract;
import com.canplay.milk.mvp.present.LoginPresenter;
import com.canplay.milk.util.PwdCheckUtil;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.NavigationBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 修改密码
 */
public class EditorPwsActivity extends BaseActivity implements LoginContract.View {

    @Inject
    LoginPresenter presenter;


    @BindView(R.id.navigationbar)
    NavigationBar navigationbar;
    @BindView(R.id.et_pws)
    ClearEditText etPws;
    @BindView(R.id.et_pws1)
    ClearEditText etPws1;
    @BindView(R.id.et_pws2)
    ClearEditText etPws2;
    @BindView(R.id.tv_sure)
    TextView tvSure;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.hint)
    TextView hint;
    @BindView(R.id.tv_forget)
    TextView tvForget;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_editor_pass);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        navigationbar.setNavigationBarListener(this);


    }

    @Override
    public void bindEvents() {
        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//忘记密码
                Intent intent=new Intent(EditorPwsActivity.this,ForgetFirstActivity.class);
                startActivity(intent);
                finish();
            }
        });
        etPws1.setOnClearEditTextListener(new ClearEditText.ClearEditTextListener() {
            @Override
            public void afterTextChanged4ClearEdit(Editable s) {
                if (TextUtil.isNotEmpty(s.toString())) {
                    hint.setVisibility(View.GONE);
                } else {
                    hint.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void changeText(CharSequence s) {

            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtil.isEmpty(etPws.getText().toString())) {
                    showToasts("请填写旧密码");
                    return;
                }
                if (TextUtil.isEmpty(etPws1.getText().toString())) {
                    showToasts("请填写新密码");
                    return;
                }
                if (TextUtil.isEmpty(etPws2.getText().toString())) {
                    showToasts("请填写确认密码");
                    return;
                }
                if (!etPws1.getText().toString().equals(etPws2.getText().toString())) {
                    showToasts("两次密码不一致");
                    return;
                }
                if (!PwdCheckUtil.isLetterDigit(etPws1.getText().toString())) {
                    showToasts("密码至少6位数且包含数字，字母");
                    return;
                }
                showProgress("修改中...");
                presenter.updatePwd(etPws.getText().toString(), etPws1.getText().toString(), etPws2.getText().toString());
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
        showToasts("修改成功");
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.FINISH, ""));


        SpUtil.getInstance().clearData();
        SpUtil.getInstance().putString("firt", "firt");
        startActivity(new Intent(this, LoginActivity.class));
        finish();
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
}
