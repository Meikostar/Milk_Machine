package com.canplay.milk.mvp.activity.home;

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
import com.canplay.milk.bean.MilkInfo;
import com.canplay.milk.bean.SetMilk;
import com.canplay.milk.mvp.activity.MainActivity;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.permission.PermissionConst;
import com.canplay.milk.permission.PermissionGen;
import com.canplay.milk.permission.PermissionSuccess;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.NavigationBar;
import com.google.zxing.client.android.activity.CaptureActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;


/**
 * 奶粉信息
 */
public class MilkDetailActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.tv_weigh)
    TextView tvWeigh;
    @BindView(R.id.tv_ml)
    TextView tvMl;
    @BindView(R.id.tv_wd)
    TextView tvWd;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.tv_sure)
    TextView tvSure;
    @BindView(R.id.tv_change)
    TextView tvChange;
    @BindView(R.id.tv_name1)
    TextView tvName1;

    @Override
    public void initViews() {
        setContentView(R.layout.acitivity_milk_detail);
        ButterKnife.bind(this);
        navigationBar.setNavigationBarListener(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        SetMilk milk = SpUtil.getInstance().getMilk();
        if(milk!=null){
            showProgress("加载中...");
            presenter.getMilkInfo(milk.milkInfoId);
        }


        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.SCAN == bean.type) {
                   String code= (String) bean.content;
                    presenter.getMilkInfoByBarCode(code);
                }else if(SubscriptionBean.MILKINFO == bean.type) {
                    String code= (String) bean.content;
                    presenter.getMilkInfo(code);
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
    private String id;
   private Subscription mSubscription;
    @Override
    public void bindEvents() {


        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MilkDetailActivity.this, MilkListActivity.class), 6);
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtil.isNotEmpty(id)){
                    showProgress("保存中...");
                    presenter.setUserMilk(id);
                }else {
                    finish();
                }

            }
        });
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                PermissionGen.with(MilkDetailActivity.this)
                        .addRequestCode(PermissionConst.REQUECT_DATE)
                        .permissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .request();
            }

            @Override
            public void navigationimg() {

            }
        });
    }


    @Override
    public void initData() {

    }

   private int status;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
                if (data != null) {
                    String content = data.getStringExtra("scan_result");
                    if(status==0){
                        showProgress("加载中...");

                        presenter.getMilkInfoByBarCode(content);
                        status=1;
                    }

//                    showToasts("扫描结果为：" + content);
//                result.setText("扫描结果为：" + content);
                }
            } else {
                MilkInfo info = (MilkInfo) data.getSerializableExtra("picked_milk");
                if (info != null) {
                    setData(info);
                }
            }


        }
    }

    @PermissionSuccess(requestCode = PermissionConst.REQUECT_DATE)
    public void requestSdcardSuccess() {
        Intent intent = new Intent(MilkDetailActivity.this, CaptureActivity.class);
         /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
         * 也可以不传这个参数
         * 不传的话  默认都为默认不震动  其他都为true
         * */

        //ZxingConfig config = new ZxingConfig();
        //config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
        //config.setPlayBeep(true);//是否播放提示音
        //config.setShake(true);//是否震动
        //config.setShowAlbum(true);//是否显示相册
        //config.setShowFlashLight(true);//是否显示闪光灯
        //intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    private int REQUEST_CODE_SCAN = 6;

    public void setData(MilkInfo info) {
        id=info.id;
        if (TextUtil.isNotEmpty(info.waterTemperature)) {
            tvWd.setText(info.waterTemperature+"℃");
        }
        if (TextUtil.isNotEmpty(info.milkWeight)) {
            tvWeigh.setText(info.milkWeight+"g");
        }
        if (TextUtil.isNotEmpty(info.waterQuantity)) {
            tvMl.setText(info.waterQuantity+"ml");
        }
        if (TextUtil.isNotEmpty(info.subName)) {
            tvName1.setText(info.subName);
        }
        if (TextUtil.isNotEmpty(info.name)) {
            tvName.setText(info.name);
        }

        if (TextUtil.isNotEmpty(info.grade)) {
            tvDetail.setText(info.grade + "段  (" + info.gradeDescription + ")");
            tvContent.setText("最佳搭配方案\n  (" + info.gradeDescription + ")");

        }
    }

    @Override
    public <T> void toEntity(T entity, int type) {
        status=0;
        dimessProgress();
        if(type==2){
            showToasts("修改成功");
            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.REFEST_SET,""));
            finish();
        }else {
            MilkInfo info = (MilkInfo) entity;
            if(info!=null){
                if(info.validStatus==1){
                    setData(info);
                }else {
                    showToasts("没有搜索的相关奶粉");
                }
            }

            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.CLOSE,""));
        }

    }

    @Override
    public void toNextStep(int type) {
        dimessProgress();
    }

    @Override
    public void showTomast(String msg) {
        status=0;
        showToasts(msg);
        dimessProgress();
    }
}
