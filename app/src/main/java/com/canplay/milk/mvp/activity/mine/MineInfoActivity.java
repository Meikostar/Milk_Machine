package com.canplay.milk.mvp.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.USER;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.LoginContract;
import com.canplay.milk.mvp.present.LoginPresenter;
import com.canplay.milk.permission.PermissionConst;
import com.canplay.milk.permission.PermissionSuccess;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.CircleTransform;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.EditorNameDialog;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.PhotoPopupWindow;
import com.canplay.milk.view.SquareTransform;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.valuesfeng.picker.Picker;
import io.valuesfeng.picker.widget.ImageLoaderEngine;
import rx.Subscription;
import rx.functions.Action1;


/**
 * 个人信息
 */
public class MineInfoActivity extends BaseActivity implements LoginContract.View{

    @Inject
    LoginPresenter presenter;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.iv_imgs)
    ImageView ivImgs;
    @BindView(R.id.et_baby)
    ClearEditText etBaby;
    @BindView(R.id.et_father)
    ClearEditText etFather;
    @BindView(R.id.et_mami)
    ClearEditText etMami;
    private EditorNameDialog dialog;
    private PhotoPopupWindow mWindowAddPhoto;
    private int sex = 0;
    private String name = "";
    private USER user;
    @Override
    public void initViews() {
        setContentView(R.layout.activity_mine_info);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);

        navigationBar.setNavigationBarListener(this);
       user= SpUtil.getInstance().getUsers();
        if(user!=null){
            if(TextUtil.isNotEmpty(user.name)){
                etBaby.setText(user.name);
            }if(TextUtil.isNotEmpty(user.fatherName)){
                etFather.setText(user.fatherName);
            }if(TextUtil.isNotEmpty(user.motherName)){
                etMami.setText(user.motherName);
            }
            Glide.with(this).load(user.imgResourceKey).asBitmap().transform(new SquareTransform(this)).placeholder(R.drawable.moren).into(ivImgs);

        }

    }

    @Override
    public void bindEvents() {
       ivImgs.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(MineInfoActivity.this,UserAvarActivity.class));
           }
       });
         navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
             @Override
             public void navigationLeft() {
                 finish();
             }

             @Override
             public void navigationRight() {
                 if(!TextUtil.isNotEmpty(etBaby.getText().toString())){
                     showToasts("baby昵称不能为空");
                     return;
                 }
              presenter.updateMyBaseInfo(etBaby.getText().toString(),etFather.getText().toString()==null?"":etFather.getText().toString(),etMami.getText().toString()==null?"":etMami.getText().toString());
             }

             @Override
             public void navigationimg() {

             }
         });

    }
    @Override
    protected void onResume() {
        super.onResume();
        File cropImage = new File(getExternalCacheDir(), "crop_image.jpg");
        if(cropImage!=null){
            Glide.with(this).load(cropImage).asBitmap().transform(new SquareTransform(this)).placeholder(R.drawable.moren).into(ivImgs);
            Glide.with(this).load(user.imgResourceKey).asBitmap().transform(new SquareTransform(this)).placeholder(R.drawable.moren).into(ivImgs);

        }
    }
    private Subscription mSubscription;
    public void initData() {
        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (bean.type == SubscriptionBean.UPDATE) {
                    presenter.getMyBaseInfo();
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

    @PermissionSuccess(requestCode = PermissionConst.REQUECT_CODE_CAMERA)
    public void requestSdcardSuccess() {
        Picker.from(this)
                .count(1)
                .enableCamera(true)
                .setEngine(new ImageLoaderEngine())
                .setAdd_watermark(false)
                .forResult(4);
    }


    @Override
    public <T> void toEntity(T entity, int type) {
        if(type==11){
            showToasts("编辑成功");
            RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.UPDATE,""));
            finish();
        }else {
            user= (USER) entity;
            if(user!=null){

                Glide.with(this).load(user.imgResourceKey).asBitmap().transform(new CircleTransform(this)).placeholder(R.drawable.moren).into(ivImgs);
                SpUtil.getInstance().putUser(user);
            }
        }

    }

    @Override
    public void showTomast(String msg) {
         showToasts(msg);
    }
}
