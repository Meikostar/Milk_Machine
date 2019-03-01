package com.canplay.milk.mvp.activity.wiki;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.bean.WIPI;
import com.canplay.milk.mvp.adapter.recycle.SearchResultAdapter;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.DivItemDecoration;
import com.canplay.milk.view.NavigationBar;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 往期百科
 */
public class PastWipiSearchActivity extends BaseActivity implements BaseContract.View{

    @Inject
    BasesPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_search)
    ClearEditText etSearch;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.super_recycle_view)
    SuperRecyclerView mSuperRecyclerView;
    private SearchResultAdapter adapter;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;
    private LinearLayoutManager mLinearLayoutManager;
    private final int TYPE_PULL_REFRESH = 1;
    private final int TYPE_PULL_MORE = 2;
    private final int TYPE_REMOVE = 3;
    private String content="";
    @Override
    public void initViews() {
        setContentView(R.layout.activity_search_pasts);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        navigationBar.setNavigationBarListener(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mSuperRecyclerView.setLayoutManager(mLinearLayoutManager);
        mSuperRecyclerView.addItemDecoration(new DivItemDecoration(2, true));
        mSuperRecyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        adapter = new SearchResultAdapter(this, 0);
        mSuperRecyclerView.setAdapter(adapter);

        // mSuperRecyclerView.setRefreshing(false);
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                // mSuperRecyclerView.showMoreProgress();

                presenter.SearchArticleList(TYPE_PULL_REFRESH,1+"",cout+"",content);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mSuperRecyclerView != null) {
                            mSuperRecyclerView.hideMoreProgress();
                        }


                    }
                }, 2000);
            }
        };
        mSuperRecyclerView.setRefreshListener(refreshListener);
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
      adapter.setClickListener(new SearchResultAdapter.OnItemClickListener() {
          @Override
          public void clickListener(int type, String data) {
//              new Intent(PastWipiActivity.this,)
//              startActivity()
          }
      });

        etSearch.setOnClearEditTextListener(new ClearEditText.ClearEditTextListener() {
            @Override
            public void afterTextChanged4ClearEdit(Editable s) {
                if(TextUtil.isNotEmpty(s.toString())){
                    content=s.toString();
                    presenter.SearchArticleList(TYPE_PULL_REFRESH,1+"",cout+"",content);
                }else {
                    content="";
                }
            }

            @Override
            public void changeText(CharSequence s) {

            }
        });
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                finish();

            }

            @Override
            public void navigationimg() {

            }
        });
    }


    @Override
    public void initData() {

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

                                presenter.SearchArticleList(TYPE_PULL_MORE,cout*currpage+"",cout+"",content);


                        }
                    }, 2000);
                }
            }, 1);
        } else {
            mSuperRecyclerView.removeMoreListener();
            mSuperRecyclerView.hideMoreProgress();

        }
    }
    private int cout=8;
    @Override
    public <T> void toEntity(T entity, int type) {
        WIPI   lists= (WIPI) entity;

        onDataLoaded(type,lists.total,lists.list);
    }

    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {
         showToasts(msg);
    }




}
