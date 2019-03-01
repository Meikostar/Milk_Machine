package com.canplay.milk.mvp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.canplay.medical.R;
import com.canplay.milk.bean.DATA;
import com.canplay.milk.bean.IMG;
import com.canplay.milk.bean.WIPI;
import com.canplay.milk.mvp.activity.ImageListWitesActivity;
import com.canplay.milk.util.DensityUtil;
import com.canplay.milk.util.TextUtil;
import com.mykar.framework.activeandroid.util.Log;

import java.util.List;


public class RecordAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> list;
    private int type=0;//1是用药记录item
    public RecordAdapter(Context mContext) {

        this.mContext = mContext;
    }

    public interface ItemCliks{
        void getItem(DATA menu, int type);//type 1表示点击事件2 表示长按事件
    }
    private ItemCliks listener;
    public void setClickListener(ItemCliks listener){
        this.listener=listener;
    }
    public void setData(List<String> list){
        this.list=list;
        notifyDataSetChanged();
    }


    public void setType(int type){
        this.type=type;

    }
    @Override
    public int getCount() {
        return list!=null?list.size():0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
       final ResultViewHolder holder;
        if (view == null){
            holder = new ResultViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent, false);
            holder.img= (ImageView) view.findViewById(R.id.iv_img);


            view.setTag(holder);
        }else{
            holder = (ResultViewHolder) view.getTag();
        }
        int screenWidth = getScreenWidth(mContext)- DensityUtil.dip2px(mContext,20); // 获取屏幕宽度
        ViewGroup.LayoutParams lp = holder.img.getLayoutParams();
        lp.width = screenWidth;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.img.setLayoutParams(lp);

        holder.img.setMaxWidth(screenWidth);
        holder.img.setMaxHeight(screenWidth * 4); //这里其实可以根据需求而定，我这里测试为最大宽度的5倍
        Glide.with(mContext).load(list.get(position)).asBitmap().placeholder(R.drawable.icon_home_pm_pic).into(holder.img);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String img_url : list) {
                    IMG tem_img = new IMG();
                    tem_img.type = IMG.IMG_URL;
                    tem_img.img_url = img_url;
                    guide_img.child_list.add(tem_img);
                }
                Intent intent = new Intent(mContext, ImageListWitesActivity.class);
                intent.putExtra("img", guide_img);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
        return view;


    }
    private IMG guide_img = new IMG();
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
    public  class ResultViewHolder{

        ImageView img;



    }
}
