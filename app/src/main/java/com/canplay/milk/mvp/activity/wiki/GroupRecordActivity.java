package com.canplay.milk.mvp.activity.wiki;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.canplay.medical.BuildConfig;
import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.BaseDailogManager;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.WIPI;
import com.canplay.milk.mvp.adapter.recycle.DataCyeleAdapter;
import com.canplay.milk.mvp.adapter.recycle.GroupRecordAdapter;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.StringUtil;
import com.canplay.milk.view.DivItemDecoration;
import com.canplay.milk.view.MarkaBaseDialog;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.ProgressDialog;
import com.canplay.milk.view.TimeSelectorDialog;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 成长记录本
 */
public class GroupRecordActivity extends BaseActivity implements BaseContract.View{

    @Inject
    BasesPresenter presenter;

    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.super_recycle_view)
    SuperRecyclerView mSuperRecyclerView;
    private GroupRecordAdapter adapter;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    private LinearLayoutManager mLinearLayoutManager;
    private final int TYPE_PULL_REFRESH = 1;
    private final int TYPE_PULL_MORE = 2;
    private final int TYPE_REMOVE = 3;
    private int cout=12;
    @Override
    public void initViews() {
        setContentView(R.layout.activity_group_record);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        navigationBar.setNavigationBarListener(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mSuperRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSuperRecyclerView.addItemDecoration(new DivItemDecoration(2, true));
        mSuperRecyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        adapter = new GroupRecordAdapter(this,0);
        mSuperRecyclerView.setAdapter(adapter);
        reflash();
         mSuperRecyclerView.setRefreshing(false);
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // mSuperRecyclerView.showMoreProgress();
                presenter.growRecordList(TYPE_PULL_REFRESH,1+"",cout+"");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSuperRecyclerView!=null){
                            mSuperRecyclerView.hideMoreProgress();
                        }


                    }
                }, 2000);
            }
        };
        mSuperRecyclerView.setRefreshListener(refreshListener);

        showPopwindow();

    }
    private void reflash() {
        if (mSuperRecyclerView != null) {
            //实现自动下拉刷新功能
            mSuperRecyclerView.getSwipeToRefresh().post(new Runnable() {
                @Override
                public void run() {
                    mSuperRecyclerView.setRefreshing(true);//执行下拉刷新的动画
                    refreshListener.onRefresh();//执行数据加载操作
                }
            });
        }
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
               startActivity(new Intent(GroupRecordActivity.this,SendRecordActivity.class));
             }

             @Override
             public void navigationimg() {

             }
         });

        adapter.setClickListener(new GroupRecordAdapter.OnItemClickListener() {
            @Override
            public void clickListener(int type, String ids) {
                id=ids;
                dialog.show();
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
                if(SubscriptionBean.GROUP==bean.type){
                    reflash();

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
   private String id;
   private MarkaBaseDialog dialog;
   public void showPopwindow(){

         dialog = BaseDailogManager.getInstance().getBuilder(this).setLayoutID(R.layout.dialog_base_detele).setOnClickListener(new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int which) {
               if(which==DialogInterface.BUTTON_POSITIVE){
                   dialog.dismiss();
                   presenter.growRecordDelete(id);
               }else {
                   dialog.dismiss();
               }
           }
       }).create();
   }

    @Override
    public <T> void toEntity(T entity, int type) {
        dimessProgress();
        if(type==8){
            showToasts("删除成功");
            reflash();
        }else {
            WIPI lists= (WIPI) entity;
            onDataLoaded(type,lists.total,lists.list);
        }



    }
    private List<WIPI> list=new ArrayList<>();
    private int currpage=1;
    public void onDataLoaded(int loadtype,final int haveNext, List<WIPI> datas) {

        if (loadtype == TYPE_PULL_REFRESH) {
            currpage=1;
            list.clear();
            for (WIPI info : datas) {
                list.add(info);
            }
        } else {
            for (WIPI info : datas) {
                list.add(info);
            }
        }
        adapter.setDatas(list);
        adapter.notifyDataSetChanged();
//        mSuperRecyclerView.setLoadingMore(false);
        mSuperRecyclerView.hideMoreProgress();

        /**
         * 判断是否需要加载更多，与服务器的总条数比
         */
        if (haveNext>list.size()) {
            mSuperRecyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                    currpage++;
                    mSuperRecyclerView.showMoreProgress();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (haveNext>list.size())
                                mSuperRecyclerView.hideMoreProgress();

                            presenter.growRecordList(TYPE_PULL_MORE,cout*currpage+"",cout+"");


                        }
                    }, 2000);
                }
            }, 1);
        } else {
            mSuperRecyclerView.removeMoreListener();
            mSuperRecyclerView.hideMoreProgress();

        }
    }
    @Override
    public void toNextStep(int type) {
        dimessProgress();
    }

    @Override
    public void showTomast(String msg) {
       showToasts(msg);
        dimessProgress();
    }




}
