package com.canplay.milk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.canplay.medical.R;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.BaseFragment;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.USER;
import com.canplay.milk.mvp.activity.account.EditorPwsActivity;
import com.canplay.milk.mvp.activity.account.ForgetPswActivity;
import com.canplay.milk.mvp.activity.account.LoginActivity;
import com.canplay.milk.mvp.activity.mine.AboutActivity;
import com.canplay.milk.mvp.activity.mine.EditorInfoActivity;
import com.canplay.milk.mvp.activity.mine.MineInfoActivity;
import com.canplay.milk.mvp.activity.mine.UpdateActivity;
import com.canplay.milk.mvp.activity.mine.UserAvarActivity;
import com.canplay.milk.mvp.activity.mine.WifiSettingActivity;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.LoginContract;
import com.canplay.milk.mvp.present.LoginPresenter;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.CircleImageView;
import com.canplay.milk.view.CircleTransform;
import com.canplay.milk.view.EditorNameDialog;
import com.canplay.milk.view.PhotoPopupWindow;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by mykar on 17/4/10.
 */
public class SetFragment extends BaseFragment implements View.OnClickListener ,LoginContract.View{

    @Inject
    LoginPresenter presenter;
    Unbinder unbinder;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.iv_img)
    CircleImageView ivImg;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.ll_wifi)
    LinearLayout llWifi;
    @BindView(R.id.ll_about)
    LinearLayout llAbout;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.ll_pasw)
    LinearLayout llPasw;
    @BindView(R.id.ll_update)
    LinearLayout llUpdate;
    @BindView(R.id.ll_change)
    LinearLayout llChange;


    private EditorNameDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set, null);
        unbinder = ButterKnife.bind(this, view);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getActivity().getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        presenter.getMyBaseInfo();
        mWindowAddPhoto = new PhotoPopupWindow(getActivity());
        mWindowAddPhoto.setCont("解除绑定", "取消");
        mWindowAddPhoto.setColor(R.color.red_pop, 0);
        initListener();
        return view;
    }

    public PhotoPopupWindow mWindowAddPhoto;

    @Override
    public void onResume() {
        super.onResume();

    }
   private Subscription mSubscription;

    private void initListener() {


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
        llWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),WifiSettingActivity.class));
            }
        });
        llAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AboutActivity.class));
            }
        });
        llUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),UpdateActivity.class));
            }
        });

        llPasw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),EditorPwsActivity.class));
            }
        });
        ivImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MineInfoActivity.class));

            }
        });
        llChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("type",1);
                startActivity(intent);


            }
        });


    }

    private void initView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if(mSubscription!=null){
            mSubscription.unsubscribe();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
    private USER user;
    @Override
    public <T> void toEntity(T entity, int type) {
        if(type==6){


//            SpUtil.getInstance().clearData();
//            SpUtil.getInstance().putString("firt","firt");
//            startActivity(new Intent(getActivity(), LoginActivity.class));
//            getActivity().finish();
        }else {
            user= (USER) entity;
            if(user!=null){
                if(TextUtil.isNotEmpty(user.name)){
                    tvName.setText(user.name);

                }

                Glide.with(this).load(user.imgResourceKey).asBitmap().transform(new CircleTransform(getActivity())).placeholder(R.drawable.moren).into(ivImg);
                SpUtil.getInstance().putUser(user);
            }

        }

    }

    @Override
    public void showTomast(String msg) {
          showToast(msg);
    }
}
