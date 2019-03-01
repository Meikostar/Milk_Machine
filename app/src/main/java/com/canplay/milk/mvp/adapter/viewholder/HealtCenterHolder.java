package com.canplay.milk.mvp.adapter.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;


/**
 * Created by mykar on 17/4/12.
 */
public class HealtCenterHolder extends RecyclerView.ViewHolder  {

    public ImageView img;
    public ImageView iv_arrow;
    public TextView name;
    public TextView phone;
    public TextView tv_address;
    public TextView tv_des;
    public Button add;
    public LinearLayout ll_bg;


    public HealtCenterHolder(View itemView,int type) {
        super(itemView);


    }

}
