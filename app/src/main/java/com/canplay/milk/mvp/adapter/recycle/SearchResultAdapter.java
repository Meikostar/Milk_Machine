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
import com.canplay.milk.mvp.activity.WebViewWitesActivity;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by mykar on 17/4/12.
 */
public class SearchResultAdapter extends BaseRecycleViewAdapter {



    private Context context;
    private int type;
    private int status;

    public SearchResultAdapter(Context context, int type) {
        this.context = context;
        this.type = type;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ite_search_result, null);

        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder holders = (ViewHolder) holder;
        final WIPI wipi= (WIPI) datas.get(position);
        if(TextUtil.isNotEmpty(wipi.title)){
            holders.tvTitle.setText(wipi.title);
        }  if(TextUtil.isNotEmpty(wipi.shortContent)){
            holders.tvContent.setText(wipi.shortContent);
        }
        Glide.with(context).load(wipi.resoureKey).asBitmap().placeholder(R.drawable.moren).into(holders.ivImg);

        holders.tvTime.setText(TimeUtil.formatTims(wipi.updateTime==0?wipi.createTime:wipi.updateTime));
        holders.tvGoDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, WebViewWitesActivity.class);
                intent.putExtra(WebViewWitesActivity.WEBTITLE,"百科详情");
                intent.putExtra("titles", TextUtil.isNotEmpty(wipi.title)?wipi.title:"今日百科");
                intent.putExtra("img", TextUtil.isNotEmpty(wipi.resoureKey)?wipi.resoureKey:"");
                intent.putExtra("contents", TextUtil.isNotEmpty(wipi.shortContent)?wipi.shortContent:"宝宝的呵护");
                intent.putExtra(WebViewWitesActivity.WEBURL,wipi.url);
                context.startActivity(intent);
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
        void clickListener(int type, String data);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.iv_img)
        ImageView ivImg;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_go_detail)
        TextView tvGoDetail;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
