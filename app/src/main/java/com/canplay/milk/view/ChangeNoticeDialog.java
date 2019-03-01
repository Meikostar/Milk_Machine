package com.canplay.milk.view;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.canplay.medical.R;

/**
 * Created by qi_fu on 2017/12/18.
 */

public class ChangeNoticeDialog {

    private  TextView tv_content;
    private  TextView tvTitle;
    private  ImageView ivImg;
    private TextView tvName;
    private TextView tvPhone;
    private  TextView tvDetail;
    private  ListView list;
    private TextView tvCancel;
    private  TextView tvAgree;
    private Context mContext;
    private View mView;
    private PopupWindow mPopupWindow;


    private BindClickListener mBindClickListener;

    private int gg;
    private int jl;
    private int kw;
    private View parentView;

    public ChangeNoticeDialog(Context mContext, View parentView) {
        this.mContext = mContext;
        this.parentView = parentView;
        initView();
        mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setFocusable(true);

    }

    public ChangeNoticeDialog setBindClickListener(BindClickListener mBindClickListener) {
        this.mBindClickListener = mBindClickListener;
        return this;
    }

    public View getView() {
        return mView;
    }


    public interface BindClickListener {
        void teaMoney(String money);
    }

    public void setTitles(String name) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(name);
    }

    private void initView() {
        mView = View.inflate(mContext, R.layout.change_notic_dialog, null);
        tvTitle = (TextView) mView.findViewById(R.id.tv_title);
        tvName = (TextView) mView.findViewById(R.id.tv_name);
        tvPhone = (TextView) mView.findViewById(R.id.tv_phone);
        tvDetail = (TextView) mView.findViewById(R.id.tv_detail);
        tvCancel = (TextView) mView.findViewById(R.id.tv_cancel);
        tvAgree = (TextView) mView.findViewById(R.id.tv_agree);
        ivImg = (ImageView) mView.findViewById(R.id.iv_img);
        list = (ListView) mView.findViewById(R.id.list);



        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBindClickListener.teaMoney("");
            }
        });
    }

    private void setData() {

    }

    public void show() {

        if (mPopupWindow != null) {
            if (mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
            } else {
//             mPopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
//             mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                mPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
            }
        }
    }

    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }
}

