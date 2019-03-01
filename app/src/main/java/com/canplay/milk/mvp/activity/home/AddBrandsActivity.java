package com.canplay.milk.mvp.activity.home;


import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.DATA;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.ListPopupWindow;
import com.canplay.milk.view.NavigationBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 新增品牌
 */
public class AddBrandsActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_brand)
    ClearEditText etBrand;
    @BindView(R.id.tv_choose)
    TextView tvChoose;
    @BindView(R.id.et_ml)
    ClearEditText etMl;
    @BindView(R.id.et_wd)
    ClearEditText etWd;
    @BindView(R.id.et_weight)
    ClearEditText etWeight;
    @BindView(R.id.ll_choose)
    LinearLayout llChoose;
    @BindView(R.id.line)
    View line;
    private ListPopupWindow popupWindow;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_add_brand);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
    }

    @Override
    public void bindEvents() {
        llChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyBoard();
                popupWindow.showAsDropDown(line);
            }
        });
        tvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyBoard();
                popupWindow.showAsDropDown(line);
            }
        });
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
             if(TextUtil.isEmpty(etBrand.getText().toString())){
                 showToasts("奶粉品牌不能为空");
                return;
             }  if(TextUtil.isEmpty(etMl.getText().toString())){
                    showToasts("冲奶参考水量不能为空");
                    return;
                }
//                if(TextUtil.isEmpty(etWd.getText().toString())){
//                    showToasts("冲奶参考水温不能为空");
//                    return;
//                }
                if(TextUtil.isEmpty(etWeight.getText().toString())){
                    showToasts("奶粉参考重量不能为空");
                    return;
                }if(TextUtil.isEmpty(tvChoose.getText().toString())){
                    showToasts("奶粉段位不能为空");
                    return;
                }
                showProgress("l添加中...");
                presenter.createUserMilk(dat.count,etWeight.getText().toString(),etMl.getText().toString(),"55",etBrand.getText().toString());
            }

            @Override
            public void navigationimg() {

            }
        });
    }
            private List<DATA> data = new ArrayList<>();

            @Override
            public void initData() {
                DATA datas = new DATA();
                datas.content = "一段";
                datas.count = "1";
                DATA data1 = new DATA();
                data1.content = "二段";
                data1.count = "2";
                DATA data2 = new DATA();
                data2.content = "三段";
                data2.count = "3";
//                DATA data3 = new DATA();
//                data3.content = "四段";
//                data3.count = "4";
                data.add(datas);
                data.add(data1);
                data.add(data2);
//                data.add(data3);
                popupWindow = new ListPopupWindow(this, data);
                popupWindow.setSureListener(new ListPopupWindow.ClickListener() {
                    @Override
                    public void clickListener(DATA menu, int poistion) {
                        dat=menu;
                        tvChoose.setText(menu.content);
                        popupWindow.dismiss();
                    }
                });
            }

            @Override
            protected void onDestroy() {
                super.onDestroy();

            }

    private DATA dat;
    @Override
    public <T> void toEntity(T entity, int type) {
        dimessProgress();
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.REFESH_MLIK,""));
        showToasts("添加成功");
        finish();
    }

    @Override
    public void toNextStep(int type) {
       dimessProgress();
    }

    @Override
    public void showTomast(String msg) {
        dimessProgress();
       showToasts(msg);
    }
}

