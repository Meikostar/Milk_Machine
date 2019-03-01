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
import com.canplay.milk.bean.Article;
import com.canplay.milk.mvp.activity.WebViewWitesActivity;
import com.canplay.milk.mvp.activity.wiki.GroupRecordActivity;
import com.canplay.milk.mvp.activity.wiki.NurseryActivity;
import com.canplay.milk.mvp.activity.wiki.PastWipiActivity;
import com.canplay.milk.mvp.activity.wiki.SeachResultActivity;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.TimeUtil;
import com.canplay.milk.view.PhotoPopupWindow;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 百科
 */
public class WikiPediaFragment extends BaseFragment implements View.OnClickListener, BaseContract.View {

    @Inject
    BasesPresenter presenter;

    Unbinder unbinder;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.tv_ym)
    TextView tvYm;
    @BindView(R.id.tv_time)
    LinearLayout llTime;
    @BindView(R.id.ll_group)
    LinearLayout llGroup;
    @BindView(R.id.bai3)
    ImageView bai3;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_times)
    TextView tvTimes;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.ll_record)
    LinearLayout llRecord;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.ll_past)
    LinearLayout llPast;
    @BindView(R.id.ll_pass)
    LinearLayout llPass;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getActivity().getApplication()).getAppComponent()).build().inject(this);
        if(presenter!=null){
            presenter.attachView(this);
            presenter.getRecommendArticle();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wikipedia_fragment, null);
        unbinder = ButterKnife.bind(this, view);


        initListener();


        return view;
    }

    public PhotoPopupWindow mWindowAddPhoto;

    @Override
    public void onResume() {
        super.onResume();
        long nextTime = SpUtil.getInstance().getNextTime();
        long time = nextTime - System.currentTimeMillis();
        if (time > 0) {
            long day = (time / (1000 * 3600 * 24)) + 1;
            tvYm.setText(day + "天");
        }else {
            tvYm.setText("无");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            long nextTime = SpUtil.getInstance().getNextTime();
            long time = nextTime - System.currentTimeMillis();
            if (time > 0) {
                long day = (time / (1000 * 3600 * 24)) + 1;
                tvYm.setText(day + "天");
            }else {
                tvYm.setText("无");
            }

        }
    }

    private void initListener() {
        llTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NurseryActivity.class));

            }
        });
        llGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GroupRecordActivity.class));
            }
        });
        llRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewWitesActivity.class);

                intent.putExtra(WebViewWitesActivity.WEBTITLE, "百科详情");
                intent.putExtra("titles", TextUtil.isNotEmpty(article.title)?article.title:"今日百科");
                intent.putExtra("img", TextUtil.isNotEmpty(article.resoureKey)?article.resoureKey:"");
                intent.putExtra("contents", TextUtil.isNotEmpty(article.shortContent)?article.shortContent:"宝宝的呵护");
                intent.putExtra("state", 1);
                intent.putExtra(WebViewWitesActivity.WEBURL, "http://39.108.15.39:41072/share.html?id=" + article.id);
                startActivity(intent);
            }
        });
        llPast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PastWipiActivity.class));
            }
        });
    }

    private void initView() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    private Article article;

    @Override
    public <T> void toEntity(T entity, int type) {
        article = (Article) entity;
        if (TextUtil.isNotEmpty(article.title)) {
            tvTitle.setText(article.title);
        }
        if (TextUtil.isNotEmpty(article.shortContent)) {
            tvContent.setText(article.shortContent);
        }
        tvTimes.setText(TimeUtil.StringData(article.updateTime));
        Glide.with(this).load(article.resoureKey).asBitmap().placeholder(R.drawable.icon_enc_pic).into(ivImg);
    }

    @Override
    public void toNextStep(int type) {

    }

    @Override
    public void showTomast(String msg) {

    }
}
