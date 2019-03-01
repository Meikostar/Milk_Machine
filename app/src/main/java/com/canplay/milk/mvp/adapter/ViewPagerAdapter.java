package com.canplay.milk.mvp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.canplay.medical.R;
import com.canplay.milk.mvp.activity.account.LoginActivity;
import com.canplay.milk.util.SpUtil;

import java.util.List;

/**
 * Created by mykar on 17/11/13.
 */


public class ViewPagerAdapter extends PagerAdapter {

    // 界面列表
    private List<View> views;
    private Activity activity;


    public ViewPagerAdapter(List<View> views, Activity activity) {
        this.views = views;
        this.activity = activity;
    }

    // 销毁arg1位置的界面
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public void finishUpdate(ViewGroup container) {
    }

    // 获得当前界面数
    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    // 初始化arg1位置的界面
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position), 0);
        if (position == views.size() - 1) {
            TextView mStartWeiboImageButton = (TextView)container.findViewById(R.id.btn_start);
            mStartWeiboImageButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 设置已经引导
                    setGuided();
                    goHome();

                }

            });
        }
        return views.get(position);
    }

    private void goHome() {
        // 跳转
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     *
     * method desc：设置已经引导过了，下次启动不用再次引导
     */
    private void setGuided() {
        SpUtil.getInstance().putString("firt","first_star");
//        ShareDataManager.getInstance().setFirstStart("HAVE");
    }

    // 判断是否由对象生成界面
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }

}
