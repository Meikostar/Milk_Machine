package com.canplay.milk.mvp.adapter.recycle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.canplay.medical.R;
import com.canplay.milk.mvp.adapter.viewholder.HealtCenterHolder;



/**
 * Created by mykar on 17/4/12.
 */
public class HealthCenterAdapter extends BaseRecycleViewAdapter {

    private Context context;
    private int type;
    private int status;
    public HealthCenterAdapter(Context context,int type) {
        this.context = context;
        this.type=type;
    }
    public void setStatus(int status){
        this.status=status;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=null;
        if(type==0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_bind_phone, null);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_bind_phone, null);
        }
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        return new HealtCenterHolder(view,type);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        HealtCenterHolder holders = (HealtCenterHolder) holder;



     }

    @Override
    public int getItemCount() {
        int count = 0;

        if (datas != null && datas.size() > 0) {
            count = datas.size();
        }

        return count;
    }

    public void setClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
    public OnItemClickListener listener;
    public interface  OnItemClickListener{
        void clickListener(int type, String data);
    }
}
