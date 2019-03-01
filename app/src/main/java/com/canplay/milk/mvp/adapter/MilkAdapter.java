package com.canplay.milk.mvp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.canplay.medical.R;
import com.canplay.milk.bean.Milk;
import com.canplay.milk.bean.MilkInfo;
import com.canplay.milk.util.PinYinUtils;
import com.canplay.milk.util.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class MilkAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 3;

    private Context mContext;
    private LayoutInflater inflater;
    private List<MilkInfo> mCities;
    private HashMap<String, Integer> letterIndexes;
    private String[] sections;
    private OnCityClickListener onCityClickListener;
    private String type = "location";
    private int status;

    private String locatedCity;
    public void setData(List<MilkInfo> mCities){
        if( this.mCities!=null){
            this.mCities.clear();
        }

        this.mCities=mCities;
        if (mCities == null){
            mCities = new ArrayList<>();
        }


        int size = mCities.size();
        letterIndexes = new HashMap<>();
        sections = new String[size];
        for (int index = 0; index < size; index++){
            //当前城市拼音首字母
            String currentLetter = PinYinUtils.getFirstLetter(mCities.get(index).pinyin.substring(0,1));
            //上个首字母，如果不存在设为""
            String previousLetter = index >= 1 ? PinYinUtils.getFirstLetter(mCities.get(index - 1).pinyin.substring(0,1)) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)){
                letterIndexes.put(currentLetter, index);
                sections[index] = currentLetter;
            }
        }
        notifyDataSetChanged();
    }

    public MilkAdapter(Context mContext, List<MilkInfo> mCities, int type) {
        status = type;
        this.mContext = mContext;
        this.mCities = mCities;
        this.inflater = LayoutInflater.from(mContext);
        if (mCities == null) {
            mCities = new ArrayList<>();
        }


        int size = mCities.size();
        letterIndexes = new HashMap<>();
        sections = new String[size];
        for (int index = 0; index < size; index++) {
            //当前城市拼音首字母
            String currentLetter = PinYinUtils.getFirstLetter(mCities.get(index).pinyin.substring(0,1));
            //上个首字母，如果不存在设为""
            String previousLetter = index >= 1 ? PinYinUtils.getFirstLetter(mCities.get(index - 1).pinyin.substring(0,1)) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)) {
                letterIndexes.put(currentLetter, index);
                sections[index] = currentLetter;
            }
        }
    }

    /**
     * 更新定位状态
     *
     * @param state
     */
    public void updateLocateState(int state, String city) {

        this.locatedCity = city;
        notifyDataSetChanged();
    }

    /**
     * 获取字母索引的位置
     *
     * @param letter
     * @return
     */
    public int getLetterPosition(String letter) {
        Integer integer = letterIndexes.get(letter);
        return integer == null ? -1 : integer;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }


    @Override
    public int getCount() {
        return mCities == null ? 0 : mCities.size();
    }

    @Override
    public MilkInfo getItem(int position) {
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
            view = View.inflate(mContext, R.layout.choose_contry_items, null);
            holder = new CityViewHolder();
            holder.letter = (TextView) view.findViewById(R.id.tv_item_city_listview_letter);
            holder.name1= (TextView) view.findViewById(R.id.tv_name1);
            holder.name2 = (TextView) view.findViewById(R.id.tv_name2);
            holder.name3 = (TextView) view.findViewById(R.id.tv_name3);
            holder.ll_bg = (LinearLayout) view.findViewById(R.id.ll_bg);
            view.setTag(holder);
        } else {
            holder = (CityViewHolder) view.getTag();
        }
        String city = "";
        if(TextUtil.isNotEmpty(mCities.get(position).name)){
            city=mCities.get(position).name;
        }

        if (position >= 1) {

            holder.name1.setText(city);
//            holder.names.setText(mCities.get(position).name);
            String filter ;
            String lastfilter = "";

                filter = mCities.get(position).pinyin.substring(0,1);
                if(position>0){
                    lastfilter = mCities.get(position-1).pinyin.substring(0,1);
                }


            String currentLetter = PinYinUtils.getFirstLetter(filter);
            String previousLetter = position >= 1 ? PinYinUtils.getFirstLetter(lastfilter) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)) {
                holder.letter.setVisibility(View.VISIBLE);
                holder.letter.setText(currentLetter.toUpperCase());
            } else {
                holder.letter.setVisibility(View.GONE);
            }
            holder.name1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCityClickListener != null) {
                        onCityClickListener.onCityClick(mCities.get(position));
                    }
                }
            });
        } else {
            holder.name1.setText(city);
//            holder.names.setText(mCities.get(position).name);
            holder.letter.setVisibility(View.VISIBLE);
            holder.letter.setText(mCities.get(position).pinyin.substring(0,1).toUpperCase());
            holder.name1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCityClickListener != null) {
                        onCityClickListener.onCityClick(mCities.get(position));
                    }
                }
            });

        }
        return view;
    }

    public static class CityViewHolder {
        TextView letter;
        TextView name1;
        TextView name2;
        TextView name3;

        LinearLayout ll_bg;
    }

    public void setOnCityClickListener(OnCityClickListener listener) {
        this.onCityClickListener = listener;
    }

    public interface OnCityClickListener {
        void onCityClick(MilkInfo name);

        void onLocateClick();
    }
}
