package com.canplay.milk.mvp.adapter;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.bean.AlarmClock;
import com.canplay.milk.bean.RemindMilk;
import com.canplay.milk.mvp.component.AlarmClockUpdateEvent;
import com.canplay.milk.util.AlarmClockOperate;
import com.canplay.milk.util.AudioPlayer;
import com.canplay.milk.util.MyUtil;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.MCheckBox;
import com.canplay.milk.view.SwipeListLayout;
import com.google.gson.Gson;
import com.google.zxing.client.android.utils.OttoAppConfig;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by honghouyang on 16/12/23.
 */

public class RemindMeasureAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<AlarmClock> list;
    private int type;
    private ListView lv_content;
    private Set<SwipeListLayout> sets = new HashSet();
    private selectItemListener listener;

    public interface selectItemListener {
        void delete(AlarmClock medicine, int type, int poistion);
    }

    public void setListener(selectItemListener listener) {
        this.listener = listener;
    }

    public void setData(List<AlarmClock> list,int type) {
        this.type = type;
        this.list = list;
        notifyDataSetChanged();
    }

    public List<AlarmClock> getDatas() {
        return list;
    }

    public RemindMeasureAdapter(Context context) {

        this.context = context;


    }

    public void setType(int type) {
        this.type = type;
    }


    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

     final    ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_remind_mesure, parent, false);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tvState = (TextView) convertView.findViewById(R.id.tv_state);
            holder.tvDay = (TextView) convertView.findViewById(R.id.tv_day);
            holder.ivchoose = (MCheckBox) convertView.findViewById(R.id.iv_choose);
            holder.llbg = (LinearLayout) convertView.findViewById(R.id.ll_bg);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final AlarmClock alarmClock = list.get(position);
        holder.llbg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delete(list.get(position), 1, position);
            }
        });

        holder.llbg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                listener.delete(list.get(position), 6, position);
                return true;
            }
        });
        if(type==1){

                holder.ivchoose.setChecked(false);
            if (list.get(position).isOnOff()) {
                updateTab(true,list.get(position));
            } else {
                updateTab(false,list.get(position));
            }

        }else {
            if (list.get(position).isOnOff()) {
                updateTab(true,list.get(position));
                holder.ivchoose.setChecked(true);
            } else {
                holder.ivchoose.setChecked(false);
                updateTab(false,list.get(position));
            }
        }

        boolean isopen = SpUtil.getInstance().getBoolean("open", true);
        if(isopen){
            holder.ivchoose.setClickable(true);
            updateTab(list.get(position).isOnOff(),list.get(position));
        }else {
            holder.ivchoose.setClickable(false);
            updateTab(false,list.get(position));
        }
        holder.ivchoose.setOnCheckClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ivchoose.isCheck()) {

                    // 闹钟状态为开的话不更新数据
//                    if (!list.get(position).isOnOff()) {
                    listener.delete(list.get(position), 3, position);
                     holder.ivchoose.setChecked(false);
                    updateTab(false,list.get(position));
//                    }
                } else {

                    // 闹钟状态为关的话不处理
//                    if (!list.get(position).isOnOff()) {
//                        return;
//                    }
                    updateTab(true,list.get(position));
                    listener.delete(list.get(position), 2, position);
                    holder.ivchoose.setChecked(true);


                }
            }
        });
        // 取得格式化后的时间
        String time = MyUtil.formatTime(alarmClock.getHour(),
                alarmClock.getMinute());
        // 设定闹钟时间的显示
        holder.tvTime.setText(time);
        // 设定重复的显示
        holder.tvDay.setText(alarmClock.getRepeat());
        // 设定标签的显示
        holder.tvState.setText(alarmClock.getTag());

//          if(TextUtil.isNotEmpty(list.get(position).items.get(0).name)){
//              holder.tvContent.setText(list.get(position).items.get(0).name);
//          } if(TextUtil.isNotEmpty(list.get(position).when)){
//            holder.tvTime.setText(list.get(position).when);
//        }
//        if(list.get(position).completedForToday){
//            holder.ivchoose.setChecked(true);
//        }else {
//            holder.ivchoose.setChecked(false);
//        }

        return convertView;
    }

    /**
     * 更新闹钟列表
     *
     * @param onOff 开关
     */
    private void updateTab(boolean onOff, AlarmClock alarmClock) {
        // 更新闹钟数据
//                        TabAlarmClockOperate.getInstance(mContext).update(onOff,
//                                alarmClock.getAlarmClockCode());
        alarmClock.setOnOff(onOff);
        Gson gson = new Gson();
        String jsonStr = gson.toJson(alarmClock); //将对象转换成Json
        // 取得格式化后的时间
        String time = MyUtil.formatTime(alarmClock.getHour(),
                alarmClock.getMinute());
        SpUtil.getInstance().putString(time, jsonStr);
        if (onOff) {
            MyUtil.startAlarmClock(context, alarmClock);
        } else {
            // 关闭闹钟
            MyUtil.cancelAlarmClock(context,
                    Integer.valueOf(alarmClock.getId()));

        }


    }

    class ViewHolder {

        TextView tvTime;
        TextView tvState;
        MCheckBox ivchoose;

        TextView tvDay;

        LinearLayout llbg;
    }
}
