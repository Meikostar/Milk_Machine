package com.canplay.milk.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.bean.RingSelectItem;
import com.canplay.milk.mvp.adapter.RingSelectAdapter;
import com.google.zxing.client.android.decode.WeacConstants;
import com.google.zxing.client.android.utils.AudioPlayer;

import java.util.Map;


/***
 * 功能描述:底部弹出的popupwindow
 * 作者:chenwei
 * 时间:2016/12/26
 * 版本:1.0
 ***/

public class RingPopupWindow extends PopupWindow{
    private View mView;
    private Context mContext;
    private ListView listView;
    private TextView tv_cancel;
    private CardView card;

    private RingSelectAdapter mSystemRingAdapter;
    public RingPopupWindow(Activity context, RingSelectAdapter adapt){
        super(context);
        mContext = context;
        mView = LayoutInflater.from(mContext).inflate(R.layout.list_popwindow, null);
        listView = (ListView) mView.findViewById(R.id.list);
        card = (CardView) mView.findViewById(R.id.card);
        mSystemRingAdapter=adapt;

        listView.setAdapter(mSystemRingAdapter);
         tv_cancel = (TextView) mView.findViewById(R.id.tv_cancel);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mSystemRingAdapter.setClickListener(new RingSelectAdapter.ItemCliks() {
            @Override
            public void getItem(int poistion, int type) {
                Map<String, String> map = mSystemRingAdapter.getItem(poistion);
                // 取得铃声名
                String ringName = map.get(WeacConstants.RING_NAME);
                // 取得播放地址
                String ringUrl = map.get(WeacConstants.RING_URL);
                // 更新当前铃声选中的位置
                mSystemRingAdapter.updateSelection(ringName);
                // 更新适配器刷新铃声列表显示
                mSystemRingAdapter.notifyDataSetChanged();
                // 设置最后一次选中的铃声选择界面位置为系统铃声界面
                RingSelectItem.getInstance().setRingPager(0);
                listener.clickListener(ringName,poistion);
                // 播放音频文件
                switch (ringUrl) {
                    case WeacConstants.DEFAULT_RING_URL:
                        // 当为默认铃声时
                        AudioPlayer.getInstance(mContext).playRaw(
                                R.raw.ring_weac_alarm_clock_default, false, false);
                        // 无铃声
                        break;
                    case WeacConstants.NO_RING_URL:
                        AudioPlayer.getInstance(mContext).stop();
                        break;
                    default:
                        AudioPlayer.getInstance(mContext).play(ringUrl, false, false);
                        break;
                }


            }
        });
    }
   public void remove(){
       card.setVisibility(View.GONE);
   }
   public void setSureListener(ClickListener listener){
       this.listener=listener;
   }
   private ClickListener listener;
   public interface  ClickListener{
       void clickListener(String menu, int poistion);
   }
    @Override
    public void showAsDropDown(View anchor) {
        init();
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        init();
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        init();
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        init();
        super.showAtLocation(parent, gravity, x, y);
    }

    private void init() {
        // TODO Auto-generated method stub
        //设置SelectPicPopupWindow的View
        this.setContentView(mView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置PopupWindow可触摸
        this.setTouchable(true);
        //设置非PopupWindow区域是否可触摸
//        this.setOutsideTouchable(false);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popwin_anim);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        backgroundAlpha((Activity) mContext,0.5f);//0.0-1.0
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                backgroundAlpha((Activity) mContext, 1f);
            }
        });
    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(Activity context, float bgAlpha)
    {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    public void showPopView(ImageView imgs) {
        showAtLocation(imgs, Gravity.BOTTOM, 0, 0);
    }

    public RingPopupWindow bindView(View view) {
        mView = view;
        return this;
    }
}
