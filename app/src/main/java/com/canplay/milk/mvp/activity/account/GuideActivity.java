package com.canplay.milk.mvp.activity.account;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.bean.Wather;
import com.canplay.milk.mvp.adapter.ViewPagerAdapter;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;


public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.ll)
    LinearLayout mLinearLayout;

    @BindView(R.id.viewpager)
    ViewPager mViewPager;

    private ViewPagerAdapter vpAdapter;

    private List<View> views;

    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;

    private Wather wather = new Wather();

    @Override
    public void initViews() {
        setContentView(R.layout.layout_activity_guide);
        ButterKnife.bind(this);

        String firt = SpUtil.getInstance().getString("firt");
        if(!TextUtil.isEmpty(firt)){
            Intent intent = new Intent(this, LoginActivity.class);
          startActivity(intent);
          finish();
        }
        wather.ml=60;
        wather.wd=40;
        SpUtil.getInstance().putWahter(wather);
        LayoutInflater inflater = LayoutInflater.from(this);

        views = new ArrayList<View>();
        // 初始化引导图片列表
        views.add(inflater.inflate(R.layout.view_guide_one, null));
        views.add(inflater.inflate(R.layout.view_guide_two, null));
        views.add(inflater.inflate(R.layout.view_guide_three, null));


        //初始化Adapter
        vpAdapter = new ViewPagerAdapter(views, this);

        mViewPager.setAdapter(vpAdapter);
        //绑定回调
        mViewPager.addOnPageChangeListener(this);



    }

    @Override
    public void bindEvents() {

    }

    @Override
    public void initData() {

        dots = new ImageView[views.size()];

        // 循环取得小点图片
        for (int i = 0; i < views.size(); i++) {
            dots[i] = (ImageView) mLinearLayout.getChildAt(i);
            dots[i].setEnabled(true); //都设为灰色
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false); //设置为白色，即选中状态

    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > views.size() - 1 || currentIndex == position) {
            return;
        }
        if(position==2){
            mLinearLayout.setVisibility(View.GONE);
        }else {
            mLinearLayout.setVisibility(View.GONE);
        }
        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }


    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int state) {
    }

    // 当当前页面被滑动时调用
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int position) {
        // 设置底部小点选中状态
        setCurrentDot(position);
    }

}
