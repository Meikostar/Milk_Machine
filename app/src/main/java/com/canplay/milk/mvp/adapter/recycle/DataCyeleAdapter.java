package com.canplay.milk.mvp.adapter.recycle;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by mykar on 17/4/12.
 */
public class DataCyeleAdapter extends BaseRecycleViewAdapter {


    private Context context;
    private int type;
    private int status;

    public DataCyeleAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (type == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_item, null);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_bind_phone, null);
        }
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder holders = (ViewHolder) holder;


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
        void clickListener(int type, String data);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_one)
        TextView tvOne;
        @BindView(R.id.tv_two)
        TextView tvTwo;
        @BindView(R.id.tv_three)
        TextView tvThree;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
