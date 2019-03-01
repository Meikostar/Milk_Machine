package com.canplay.milk.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.canplay.medical.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by syj on 2016/11/23.
 */
public class PopView_TimeRecord extends BasePopView {


    @BindView(R.id.ll_all)
    LinearLayout tvAll;
    @BindView(R.id.ll_press)
    LinearLayout tvPress;
    @BindView(R.id.ll_eat)
    LinearLayout tvEat;
    @BindView(R.id.ll_sugar)
    LinearLayout tvSugar;


    public PopView_TimeRecord(Activity activity, int type) {
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
        View popView = infalter.inflate(R.layout.popview_navigationbars, null);
        ButterKnife.bind(this, popView);
        tvAll.setOnClickListener(this);
        tvPress.setOnClickListener(this);
        tvEat.setOnClickListener(this);
        tvSugar.setOnClickListener(this);


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
            case R.id.ll_all:
                listeners.clickListener(0);
                break;
            case R.id.ll_press:
                listeners.clickListener(1);
                break;
            case R.id.ll_eat:
                listeners.clickListener(2);
                break;
            case R.id.ll_sugar:
                listeners.clickListener(3);
                break;

        }

    }

    public void setView(View view) {
        line = view;
    }


}
