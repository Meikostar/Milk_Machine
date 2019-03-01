package com.canplay.milk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.canplay.medical.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/***
 * 功能描述:
 * 作者:chenwei
 * 时间:2016/12/20
 * 版本:
 ***/
public class BaseSelector extends LinearLayout {
    private Context mContext;
    private boolean isChecked=false;
    private ImageView check;
    private boolean canNoClick;
    private CompoundButton.OnCheckedChangeListener mListener;
    private OnClickListener mClickListener;
    private  int hour;
    private  int minter;
    private  int type;
    private  int hourColor;

    public BaseSelector(Context context) {
        super(context);
    }

    public BaseSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        TypedArray mTypedArray = null;
        if (attrs!=null){
            mTypedArray = getResources().obtainAttributes(attrs,
                    R.styleable.BaseSelector);
            hour = mTypedArray.getInteger(R.styleable.BaseSelector_hours,12);
            hourColor = mTypedArray.getResourceId(R.styleable.BaseSelector_hourColors,0);
            minter=mTypedArray.getInteger(R.styleable.BaseSelector_minters,0);
            type=mTypedArray.getInteger(R.styleable.BaseSelector_typeps,0);

        }
        init();
    }
    private CycleWheelViewbig mCycleWheelViewBase;

    public void init(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
       View mView = inflater.inflate( R.layout.base_selector, this);
        mCycleWheelViewBase = (CycleWheelViewbig)mView.findViewById(R.id.cwv_base);

        setData();

    }
    private String months;
    private List<String> data1=new ArrayList<>();
    private List<String> data2=new ArrayList<>();
    private List<String> data3=new ArrayList<>();
    /**
     * 数据
     */
    private void setData(){
        List<String> list =new ArrayList<>();
        data1.add("高");
        data1.add("中");
        data1.add("低");
        data3.add("40");
        data3.add("43");
        data3.add("45");
        data3.add("48");
        data2.add("60");
        data2.add("90");
        data2.add("120");
        data2.add("150");
        data2.add("180");
        data2.add("210");
        data2.add("240");


        if(type==0){
            mCycleWheelViewBase.setLabels(data3,null);
        }else if(type==1){
            mCycleWheelViewBase.setLabels(data1,null);
        }else if(type==2){
            mCycleWheelViewBase.setLabels(data2,null);
        }

        try {
            mCycleWheelViewBase.setWheelSize(3);
        }catch (CycleWheelViewbig.CycleWheelViewException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH");
        if(hour==12){
            String date = sDateFormat.format(new Date());
            hour=Integer.valueOf(date);
        }
        if(hour==00){
            hour=23;
        }
        mCycleWheelViewBase.setLabelSelectSize(24f);

        mCycleWheelViewBase.setLabelSize(24f);
        mCycleWheelViewBase.setAlphaGradual(0.7f);
        mCycleWheelViewBase.setCycleEnable(true);
        mCycleWheelViewBase.selection(hour+1);
        mCycleWheelViewBase.setLabelColor(Color.parseColor("#9fa0a0"));
        mCycleWheelViewBase.setDivider(Color.parseColor("#3a3a39"), 1);
        mCycleWheelViewBase.setLabelSelectColor(Color.parseColor("#3a3a39"));
        mCycleWheelViewBase.setSolid(Color.WHITE, Color.WHITE);
    }


    public String getSelector(){
        String data=mCycleWheelViewBase.getSelectLabel();

        return data;
    }
    public String getData(){
        return mCycleWheelViewBase.getSelectLabel();
    }


    public void setData(int type,String content){
        if(type==0){
            if(content.equals("40")){
                mCycleWheelViewBase.selection(0);
            }else if(content.equals("43")){
                mCycleWheelViewBase.selection(1);
            }else if(content.equals("45")){
                mCycleWheelViewBase.selection(2);
            }else {
                mCycleWheelViewBase.selection(3);
            }

        }else if(type==1){
            if(content.equals("high")){
                mCycleWheelViewBase.selection(0);
            }else if(content.equals("middle")){
                mCycleWheelViewBase.selection(1);
            }else {
                mCycleWheelViewBase.selection(2);
            }
//            mCycleWheelViewBase.setLabels(data1,null);
        }else if(type==2){
            int i=0;
            for(String cont:data2){
                if(cont.equals(content)){
                    mCycleWheelViewBase.selection(i);
                    return;
                }
                i++;
            }
//            mCycleWheelViewBase.setLabels(data2,null);
        }
    }

}
