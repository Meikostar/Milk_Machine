package com.canplay.milk.mvp.activity.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.USER;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.LoginContract;
import com.canplay.milk.mvp.present.LoginPresenter;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.TimeUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.TimeSelectorDialog;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 修改质料
 */
public class EditorInfoActivity extends BaseActivity implements LoginContract.View{

    @Inject
    LoginPresenter presenter;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_name)
    ClearEditText etName;
    @BindView(R.id.et_baby)
    ClearEditText etBaby;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    private int sex = 0;
    private String name = "";
    private TimeSelectorDialog selectorDialog;
    private  String time;
    @Override
    public void initViews() {
        setContentView(R.layout.editor_info);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        selectorDialog = new TimeSelectorDialog(EditorInfoActivity.this);
        selectorDialog.setDate(new Date(System.currentTimeMillis()))
                .setBindClickListener(new TimeSelectorDialog.BindClickListener() {
                    @Override
                    public void time(String data,int poition,String times,String timess) {
                        time=data;
                        tvDate.setText(timess);
                    }
                });


    }

    @Override
    public void bindEvents() {
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                presenter.EditorMyBaseInfo(etName.getText().toString(),etBaby.getText().toString()==null?"":etBaby.getText().toString(),tvDate.getText().toString()==null?TimeUtil.formatToName(Long.valueOf(user.birthday)):time);
            }

            @Override
            public void navigationimg() {

            }
        });
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyBoard();
                selectorDialog.show(findViewById(R.id.tv_add));
            }
        });
    }

    private USER user;
    public void initData() {
        user= SpUtil.getInstance().getUsers();
        if(user!=null) {
            if (TextUtil.isNotEmpty(user.name)) {
                etName.setText(user.name);
            }
            if (TextUtil.isNotEmpty(user.weight)) {
                etBaby.setText(user.weight);
            }
            time=TimeUtil.formatToName(Long.valueOf(user.birthday));
            tvDate.setText(TimeUtil.formatTims(Long.valueOf(user.birthday)));
        }
    }


    @Override
    public <T> void toEntity(T entity, int type) {
        showToasts("修改成功");
        closeKeyBoard();
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.UPDATE,""));
         finish();
    }

    @Override
    public void showTomast(String msg) {
     showToasts(msg);
    }
}
