package com.canplay.milk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.canplay.medical.R;


/**
 * Created by yunshang on 2016/12/20.
 */

public class ThreeNevgBar extends LinearLayout implements View.OnClickListener {

    private RadioButton rdOne;

    private  RadioButton rdTwo;
    private  RadioButton rdThree;
    private  RadioGroup rdGroup;
    private Context context;
    private View view;
    private boolean type;
    private OnChangeListener mListener;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rd_one:
                rdGroup.check(R.id.rd_one);

                if (mListener!=null){
                    mListener.onChagne(0);
                }
                break;
            case R.id.rd_two:
                rdGroup.check(R.id.rd_two);

                if (mListener!=null){
                    mListener.onChagne(1);
                }
                break;
            case R.id.rd_three:
                rdGroup.check(R.id.rd_three);

                if (mListener!=null){
                    mListener.onChagne(2);
                }
                break;
        }
    }

    public void setTitle2(String title1, String title2) {
        rdOne.setText(title1);
        rdTwo.setText(title2);

    }

    public ThreeNevgBar(Context context) {
        this(context, null);
    }

    public ThreeNevgBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThreeNevgBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray mTypedArray = null;
        if (attrs != null) {
            mTypedArray = getResources().obtainAttributes(attrs,
                    R.styleable.ThreeNevgBar);
            type = mTypedArray.getBoolean(R.styleable.ThreeNevgBar_types, true);

        }
        init();
        if (mTypedArray != null) {
            mTypedArray.recycle();
        }
    }

    private void init() {

        view = LayoutInflater.from(context).inflate(R.layout.nevagbar_two, this);
        rdOne = (RadioButton) view.findViewById(R.id.rd_one);
        rdTwo = (RadioButton) view.findViewById(R.id.rd_two);
        rdThree= (RadioButton) view.findViewById(R.id.rd_three);
        rdGroup = (RadioGroup) view.findViewById(R.id.rd_group);


        rdGroup.check(R.id.rd_one);
        rdOne.setOnClickListener(this);
        rdTwo.setOnClickListener(this);
        rdThree.setOnClickListener(this);


    }

    public void setOnChangeListener(OnChangeListener listener) {
        this.mListener = listener;
    }
}
