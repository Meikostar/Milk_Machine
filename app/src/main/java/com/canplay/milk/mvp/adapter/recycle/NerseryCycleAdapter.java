package com.canplay.milk.mvp.adapter.recycle;

import android.content.Context;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.bean.Vaccines;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by mykar on 17/4/12.
 */
public class NerseryCycleAdapter extends BaseRecycleViewAdapter {



    private Context context;
    private int type;
    private int status;

    public NerseryCycleAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nersery, null);

        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder holders = (ViewHolder) holder;
        final Vaccines vaccines= (Vaccines) datas.get(position);


            if(vaccines.types!=0){
                holders.tvTime.setText(vaccines.typeName+"  "+vaccines.currentDate);
                holders.tvTime.setVisibility(View.VISIBLE);
            }else {
                holders.tvTime.setVisibility(View.GONE);
            }

        if(TextUtil.isNotEmpty(vaccines.name)){
          holders.tvName.setText(vaccines.name);
            if(vaccines.frequency==0){
               holders.tv_type.setText("");
            }else {
                holders.tv_type.setText("第"+vaccines.frequency+"针");
            }
            if (vaccines.status.equals("1")){
                holders.ivCheck.setImageResource(R.drawable.gou);
            }else {
                holders.ivCheck.setImageResource(R.drawable.gou1);
            }
        }
        if(vaccines.isNecessary==1){
          holders.tv_types.setVisibility(View.VISIBLE);
        }else {
            holders.tv_types.setVisibility(View.GONE);
        }
        holders.llBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.clickListener(vaccines.status.equals("0")?"1":"0",vaccines.id);
            }
        });


    }

    @Override
    public int getItemCount() {
        int count = 0;

        if (datas != null && datas.size() > 0) {
            count = datas.size();
        }

        return count;
    }

    public void setClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public OnItemClickListener listener;

    public interface OnItemClickListener {
        void clickListener(String type, String data);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_type)
        TextView tv_type;
        @BindView(R.id.tv_types)
        TextView tv_types;
        @BindView(R.id.iv_check)
        ImageView ivCheck;
        @BindView(R.id.ll_bg)
        LinearLayout llBg;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
