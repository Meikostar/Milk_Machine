package com.canplay.milk.mvp.activity.wiki;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.ShareBean;
import com.canplay.milk.bean.WIPI;
import com.canplay.milk.mvp.adapter.RecordAdapter;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.ThirdShareManager;
import com.canplay.milk.util.TimeUtil;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.PhotoPopupWindow;
import com.canplay.milk.view.SharePopupWindow;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 成长记录本
 */
public class PreviewRecordActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.listview_all_city)
    ListView listView;
    @BindView(R.id.tv_editor)
    TextView tvEditor;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;
    private RecordAdapter adapter;
    private WIPI wipi;
    private List<String> data=new ArrayList<>();
    @Override
    public void initViews() {
        setContentView(R.layout.activity_preview_record);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        wipi= (WIPI) getIntent().getSerializableExtra("data");

        adapter=new RecordAdapter(this);
        listView.setAdapter(adapter);
        if(wipi!=null){
            data.clear();
            if(TextUtil.isNotEmpty(wipi.content)){
                tvContent.setText(wipi.content);
            }
            tvTime.setText(TimeUtil.formatTims(wipi.updateTime==0?wipi.createTime:wipi.updateTime));
            if(TextUtil.isNotEmpty(wipi.imgResourceKeys)){
                String[] split = wipi.imgResourceKeys.split(",");
                for(int i=0;i<split.length;i++){
                    data.add(split[i]);
                }
                adapter.setData(data);
            }
        }

        sharePopupWindow = new SharePopupWindow(this);
        sharePopupWindow.setSureListener(new SharePopupWindow.ClickListener() {
            @Override
            public void clickListener(int type) {
                ShareBean shareBean = new ShareBean();
                shareBean.content_= "成长记录分享啦！";
                shareBean.title_=  "成长记录分享啦！";
                shareBean.img_="";
                shareBean.url_="http://39.108.15.39:41072/share.html?id=36&from=singlemessage";
                if(type==1){
                    ThirdShareManager.getInstance().shareWeChat(shareBean,false);
                }else {
                    ThirdShareManager.getInstance().shareWeChat(shareBean,true);
                }
                sharePopupWindow.dismiss();
            }
        });
    }
    private SharePopupWindow sharePopupWindow;
    @Override
    public void bindEvents() {
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                sharePopupWindow.showAsDropDown(navigationBar);
            }

            @Override
            public void navigationimg() {

            }
        });
        tvEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreviewRecordActivity.this, SendRecordActivity.class);
                intent.putExtra("data",wipi);
                startActivity(intent);
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
                if (SubscriptionBean.GROUP == bean.type) {
                   presenter.growRecordDetail(wipi.id);

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
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }


    @Override
    public <T> void toEntity(T entity, int type) {
         wipi = (WIPI) entity;

        if(wipi!=null){
            data.clear();
            if(TextUtil.isNotEmpty(wipi.content)){
                tvContent.setText(wipi.content);
            }
            tvTime.setText(TimeUtil.formatTims(wipi.updateTime==0?wipi.createTime:wipi.updateTime));
            if(TextUtil.isNotEmpty(wipi.imgResourceKeys)){
                String[] split = wipi.imgResourceKeys.split(",");
                for(int i=0;i<split.length;i++){
                    data.add(split[i]);
                }
                adapter.setData(data);
            }
        }
    }

    private List<WIPI> list = new ArrayList<>();
    private int currpage = 1;

    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {

    }



}
