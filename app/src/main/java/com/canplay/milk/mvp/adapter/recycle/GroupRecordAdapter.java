package com.canplay.milk.mvp.adapter.recycle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.canplay.medical.R;
import com.canplay.milk.bean.WIPI;
import com.canplay.milk.mvp.activity.wiki.PreviewRecordActivity;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.TimeUtil;
import com.canplay.milk.view.CircleTransform;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by mykar on 17/4/12.
 */
public class GroupRecordAdapter extends BaseRecycleViewAdapter {


    private Context context;
    private int type;
    private int status;

    public GroupRecordAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, null);

        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder holders = (ViewHolder) holder;
        final WIPI wipi = (WIPI) datas.get(position);
        holders.txtName.setText(TimeUtil.formatTims(wipi.createTime));
        if (TextUtil.isNotEmpty(wipi.content)) {
            holders.txtDetail.setText(wipi.content);
        }
        holders.llbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PreviewRecordActivity.class);
                intent.putExtra("data",wipi);
                context.startActivity(intent);
            }
        });
        holders.llbg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.clickListener(position,wipi.id);
                return true;
            }
        });
        if (TextUtil.isNotEmpty(wipi.imgResourceKeys)) {
            String[] split = wipi.imgResourceKeys.split(",");
            if (split.length == 1) {
                holders.ivImg1.setVisibility(View.GONE);
                holders.ivImg2.setVisibility(View.GONE);
                holders.ivImg3.setVisibility(View.VISIBLE);
                Glide.with(context).load(split[0]).asBitmap().placeholder(R.drawable.moren).transform(new CircleTransform(context)).into(holders.ivImg3);
            } else if (split.length == 2) {
                holders.ivImg1.setVisibility(View.GONE);
                holders.ivImg2.setVisibility(View.VISIBLE);
                holders.ivImg3.setVisibility(View.VISIBLE);
                Glide.with(context).load(split[0]).asBitmap().placeholder(R.drawable.moren).transform(new CircleTransform(context)).into(holders.ivImg3);
                Glide.with(context).load(split[1]).asBitmap().placeholder(R.drawable.moren).transform(new CircleTransform(context)).into(holders.ivImg2);
            } else {
                holders.ivImg1.setVisibility(View.VISIBLE);
                holders.ivImg2.setVisibility(View.VISIBLE);
                holders.ivImg3.setVisibility(View.VISIBLE);
                Glide.with(context).load(split[0]).asBitmap().placeholder(R.drawable.moren).transform(new CircleTransform(context)).into(holders.ivImg1);
                Glide.with(context).load(split[1]).asBitmap().placeholder(R.drawable.moren).transform(new CircleTransform(context)).into(holders.ivImg2);
                Glide.with(context).load(split[2]).asBitmap().placeholder(R.drawable.moren).transform(new CircleTransform(context)).into(holders.ivImg3);
            }
        }

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
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.txt_detail)
        TextView txtDetail;
        @BindView(R.id.iv_img1)
        ImageView ivImg1;
        @BindView(R.id.iv_img2)
        ImageView ivImg2;
        @BindView(R.id.iv_img3)
        ImageView ivImg3;
        @BindView(R.id.ll_bg)
        LinearLayout llbg;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
