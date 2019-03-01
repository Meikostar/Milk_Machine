package com.canplay.milk.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.canplay.medical.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/***
 * 功能描述:时间选择器
 * 作者:J
 * 时间:2016/9/1
 * 版本:
 ***/
public class HintDialogone {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ll_message)
    LinearLayout llMessage;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.bt_left)
    Button btLeft;
    @BindView(R.id.bt_right)
    Button btRight;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    private Context mContext;
    private View mView;
    private PopupWindow mPopupWindow;

    private int type = 0;//0代表 断开提示1代表消毒好了提示2 参数以保存

    public HintDialogone(Context mContext, int type) {
        this.type = type;
        this.mContext = mContext;

    }


    private int status = 0;


    private BindClickListener mBindClickListener;

    public HintDialogone setBindClickListener(BindClickListener mBindClickListener) {
        this.mBindClickListener = mBindClickListener;
        return this;
    }

    public interface BindClickListener {
        void clicks(int type,int right);
    }

    private void initView() {
        mView = View.inflate(mContext, R.layout.base_dailog_pop, null);
        ButterKnife.bind(this, mView);
        if (type == 1) {
            tvContent.setText("接口已经开始高温消毒，消毒后可冲奶");
            btLeft.setText("确定");
            btRight.setText("关闭");
        } else if (type == 2) {
            tvContent.setText("参数设置已保存");
            btRight.setVisibility(View.GONE);
            btLeft.setText("关闭");
        }else if(type==4){
            tvContent.setText("设备需要高温消毒，是否进行自清洗？");
            btLeft.setText("确定");
            btRight.setText("取消");
        }
        btRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBindClickListener.clicks(type,2);
                dismiss();
            }
        });
        btLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBindClickListener.clicks(type,1);
                dismiss();
            }
        });
    }


    public void show(View parentView) {
        if (mView == null) {
            initView();
        }
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setFocusable(true);
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else {
                mPopupWindow.showAtLocation(parentView, Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        }else {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else {
                mPopupWindow.showAtLocation(parentView, Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        }


    }
    public boolean isShow(){
        return mPopupWindow==null?false:mPopupWindow.isShowing();
    }

    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

}
