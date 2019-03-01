package com.canplay.milk.mvp.activity.account;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.NavigationBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 更换手机号
 */

public class ChangePhoneActivity extends BaseActivity {

    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_phone)
    ClearEditText etPhone;
    @BindView(R.id.tv_getcode)
    TextView tvGetcode;
    @BindView(R.id.et_code)
    ClearEditText etCode;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    private TimeCount timeCount;
    private boolean is_time;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_change_phone);
        ButterKnife.bind(this);
         navigationBar.setNavigationBarListener(this);
        //计时器
        timeCount = new TimeCount(60000, 1000);
    }


    private int REQUEST_CODE_SCAN = 6;

    @Override
    public void bindEvents() {

    }

    @Override
    public void initData() {

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
            is_time = false;
            tvGetcode.setText(R.string.chongxinhuoqu);
            tvGetcode.setEnabled(true);
        }
    }


}
