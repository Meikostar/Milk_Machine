package com.canplay.milk.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by syj on 2016/11/23.
 */
public class PopView_NavigationBar_Menu extends BasePopView {


    @BindView(R.id.ll_ring)
    LinearLayout llRing;
    @BindView(R.id.ll_wifi)
    LinearLayout llWifi;
    @BindView(R.id.tv_new)
    TextView tvNew;
    @BindView(R.id.ll_water)
    LinearLayout llWater;
    @BindView(R.id.ll_milk)
    LinearLayout llMilk;

    public PopView_NavigationBar_Menu(Activity activity, int type) {
        super(activity);
        this.type = type;
    }

    public ItemCliskListeners listeners;

    public interface ItemCliskListeners {
        void clickListener(int poistion);
    }

    public void setClickListener(ItemCliskListeners listener) {
        listeners = listener;
    }

    @Override
    protected View initPopView(LayoutInflater infalter) {
        View popView = infalter.inflate(R.layout.popview_navigationbar_menu, null);
        ButterKnife.bind(this, popView);
        llRing.setOnClickListener(this);
        llWifi.setOnClickListener(this);
        llWater.setOnClickListener(this);
        llMilk.setOnClickListener(this);


        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return popView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_ring:
                listeners.clickListener(0);
                break;
            case R.id.ll_wifi:
                listeners.clickListener(1);
                break;
            case R.id.ll_water:
                listeners.clickListener(2);
                break;
            case R.id.ll_milk:
                listeners.clickListener(3);
                break;
        }

    }

    public void setView(View view) {
        line = view;
    }

}
