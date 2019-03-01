package com.canplay.milk.mvp.activity.account;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.NavigationBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

public class ForgetFirstActivity extends BaseActivity {


    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_registered)
    TextView tvRegistered;
    @BindView(R.id.tv_next)
    TextView tvNext;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_forget_first);
        ButterKnife.bind(this);
        navigationBar.setNavigationBarListener(this);
    }

    private String username;

    @Override
    public void bindEvents() {

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(ForgetFirstActivity.this,ForgetPswActivity.class));
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
                if(SubscriptionBean.FINISH==bean.type){
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
        if(mSubscription!=null){
            mSubscription.unsubscribe();
        }
    }


}
