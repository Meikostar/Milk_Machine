package com.canplay.milk.mvp.activity.wiki;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.bean.HOME;
import com.canplay.milk.bean.Vaccines;
import com.canplay.milk.mvp.adapter.recycle.NerseryCycleAdapter;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.TimeUtil;
import com.canplay.milk.view.DivItemDecoration;
import com.canplay.milk.view.NavigationBar;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 育苗助手
 */
public class NurseryActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.ll_my)
    LinearLayout llMy;
    @BindView(R.id.super_recycle_view)
    SuperRecyclerView mSuperRecyclerView;
    private NerseryCycleAdapter adapter;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    private LinearLayoutManager mLinearLayoutManager;
    private final int TYPE_PULL_REFRESH = 1;
    private final int TYPE_PULL_MORE = 2;
    private final int TYPE_REMOVE = 3;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_nersery);
        ButterKnife.bind(this);
        navigationBar.setNavigationBarListener(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mSuperRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSuperRecyclerView.addItemDecoration(new DivItemDecoration(2, true));
        mSuperRecyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        adapter = new NerseryCycleAdapter(this, 0);
        mSuperRecyclerView.setAdapter(adapter);
        presenter.getUserVaccineList();
        showProgress("加载中...");
//        reflash();
        mSuperRecyclerView.setRefreshing(false);
//        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
//                // mSuperRecyclerView.showMoreProgress();
//                presenter.getUserVaccineList();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (mSuperRecyclerView != null) {
//                            mSuperRecyclerView.hideMoreProgress();
//                        }
//
//
//                    }
//                }, 2000);
//            }
//        };
//        mSuperRecyclerView.setRefreshListener(refreshListener);
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
        adapter.setClickListener(new NerseryCycleAdapter.OnItemClickListener() {
            @Override
            public void clickListener(String type, String data) {
                showProgress("修改中...");
                presenter.updateUserVaccineStatus(data, type);
            }
        });
    }


    @Override
    public void initData() {
        long nextTime = SpUtil.getInstance().getNextTime();
        long time = nextTime - System.currentTimeMillis();
        if (time > 0) {
            long day = time / (1000 * 3600 * 24) + 1;
            tvContent.setText("下一次疫苗时间:  " + TimeUtil.formatToMD(nextTime));
            tvTime.setText(day + "天");
        }else {
            tvTime.setText("无");
        }
    }


    private List<Vaccines> data = new ArrayList<>();

    @Override
    public <T> void toEntity(T entity, int type) {
        if (type == 5) {
            HOME  home = (HOME) entity;
            if (home != null) {

                SpUtil.getInstance().putLong("nextTime", home.nextTime);
                long nextTime = home.nextTime;
                long time = nextTime - System.currentTimeMillis();
                if (time > 0) {
                    long day = time / (1000 * 3600 * 24);
                    tvTime.setText(day+1 + "天");
                    BaseApplication.day=day+"";
                }else {
                    tvTime.setText("无");
                }
            }
        }  else if (type == 14) {
            presenter.getUserVaccineList();
            presenter.getHomeInfo();

        } else {
            dimessProgress();
            data.clear();
            List<Vaccines> list = (List<Vaccines>) entity;
            if (list != null) {

                for (Vaccines vac : list) {
                    int a = 0;
                    for (Vaccines vaccines : vac.userVaccineList) {
                        if (a == 0) {
                            vaccines.types = 1;
                        } else {
                            vaccines.types = 0;
                        }
                        vaccines.typeName = vac.typeName;
                        vaccines.currentDate = vac.currentDate;
                        data.add(vaccines);
                        a++;
                    }

                }
            }
            adapter.setDatas(data);
            adapter.notifyDataSetChanged();
        }


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
