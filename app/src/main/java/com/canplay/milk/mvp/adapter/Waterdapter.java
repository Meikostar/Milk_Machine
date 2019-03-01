package com.canplay.milk.mvp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.bean.DATA;
import com.canplay.milk.bean.MilkInfo;
import com.canplay.milk.util.PinYinUtils;
import com.canplay.milk.util.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class Waterdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 3;

    private Context mContext;
    private List<DATA> mCities;
    private OnCityClickListener onCityClickListener;

    public void setData(List<DATA> mCities){


        this.mCities=mCities;

        notifyDataSetChanged();
    }

    public Waterdapter(Context mContext) {

        this.mContext = mContext;

    }


   public List<DATA> getData(){
       return mCities;
   }

    @Override
    public int getCount() {
        return mCities == null ? 0 : mCities.size();
    }

    @Override
    public DATA getItem(int position) {
        return mCities == null ? null : mCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        CityViewHolder holder;

        if (view == null) {
            view = View.inflate(mContext, R.layout.water_item, null);
            holder = new CityViewHolder();
            holder.name1= (TextView) view.findViewById(R.id.tv_name);
            holder.img = (ImageView) view.findViewById(R.id.img);
            holder.ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
            view.setTag(holder);
        } else {
            holder = (CityViewHolder) view.getTag();
        }
        if(mCities.get(position).isCheck){
            holder.img.setVisibility(View.VISIBLE);
        }else {
            holder.img.setVisibility(View.GONE);
        }
        if(TextUtil.isNotEmpty(mCities.get(position).content)){
            String city=mCities.get(position).content;
            holder.name1.setText(city+"°C");
        }

        holder.ll_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=0;
                 for(DATA data:mCities){
                    if(position==i){
                        data.isCheck=true;
                    }else {
                        if(data.isCheck){
                            data.isCheck=false;
                        }
                    }
                    i++;
                 }
                 notifyDataSetChanged();
            }
        });
        return view;
    }

    public static class CityViewHolder {
        TextView letter;
        TextView name1;
        ImageView img;

        LinearLayout ll_bg;
    }

    public void setOnCityClickListener(OnCityClickListener listener) {
        this.onCityClickListener = listener;
    }

    public interface OnCityClickListener {
        void onCityClick(MilkInfo name);

    }
}
