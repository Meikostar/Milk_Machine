package com.canplay.milk.mvp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.bean.DATA;
import com.canplay.milk.util.TextUtil;

import java.util.List;

import butterknife.ButterKnife;


public class GroupRecordAdapter extends BaseAdapter {
    private Context mContext;
    private List<DATA> list;
    private int type=0;//1是用药记录item
    public GroupRecordAdapter(Context mContext) {

        this.mContext = mContext;
    }

    public interface ItemCliks{
        void getItem(DATA menu, int type);//type 1表示点击事件2 表示长按事件
    }
    private ItemCliks listener;
    public void setClickListener(ItemCliks listener){
        this.listener=listener;
    }
    public void setData(List<DATA> list){
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
        if (view == null) {

            view = LayoutInflater.from(mContext).inflate(R.layout.item_group, parent, false);
            holder = new ResultViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ResultViewHolder) view.getTag();
        }

        return view;


    }

    public  class ResultViewHolder{

        TextView data;
        ResultViewHolder(View view) {
            ButterKnife.bind(this, view);
        }


    }
}
