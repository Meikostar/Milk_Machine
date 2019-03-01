package com.canplay.milk.mvp.activity.account;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.MCheckBox;
import com.canplay.milk.view.NavigationBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisteredThreeActivity extends BaseActivity {


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
    private TimeCount timeCount;
    private String jobId;

    @Override
    public void initViews() {
        setContentView(R.layout.acitivity_register_three);
        ButterKnife.bind(this);
        navigationBar.setNavigationBarListener(this);
        //计时器
        timeCount = new TimeCount(60000, 1000);
        tvSave.setEnabled(false);
    }

    @Override
    public void bindEvents() {

        tvGetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etPhone.getText().toString();
                if (TextUtil.isNotEmpty(phone) && phone.length() == 11) {


                }
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                }
            }

            @Override
            public void changeText(CharSequence s) {

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


    public void isSelect(boolean choose) {
        if (choose) {
            tvSave.setEnabled(true);
            tvSave.setBackground(getResources().getDrawable(R.drawable.login_selector));

        } else {
            tvSave.setEnabled(false);
        }

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
