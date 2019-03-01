package com.canplay.milk.mvp.activity.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.MilkInfo;
import com.canplay.milk.mvp.activity.wiki.SeachResultActivity;
import com.canplay.milk.mvp.adapter.MilkAdapter;
import com.canplay.milk.mvp.adapter.MilkAdapters;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.permission.PermissionConst;
import com.canplay.milk.permission.PermissionGen;
import com.canplay.milk.permission.PermissionSuccess;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.SideLetterBars;
import com.google.zxing.client.android.activity.CaptureActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 奶粉列表
 */
public class SearchMilkActivity extends BaseActivity implements NavigationBar.NavigationBarListener  , BaseContract.View {

@Inject
BasesPresenter presenter;
    public static final String KEY_PICKED_CITY = "picked_city";
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_search)
    ClearEditText etSearch;
    @BindView(R.id.ll_bg)
    LinearLayout llBg;
    @BindView(R.id.listview_all_city)
    ListView mListView;
    @BindView(R.id.side_letter_bars)
    SideLetterBars mLetterBar;
    @BindView(R.id.tv_letter_overlay)
    TextView overlay;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;


    private MilkAdapters mCityAdapter;


   private String name;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_list_milk);
        ButterKnife.bind(this);

        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        name=getIntent().getStringExtra("name");

        if(TextUtil.isNotEmpty(name)){
            etSearch.setText(name);
            showProgress("加载中...");
            presenter.listMilkInfo(name, "1", "100");
        }
        etSearch.clearFocus();
        closeKeyBoard();
        mLetterBar = (SideLetterBars) findViewById(R.id.side_letter_bars);
        mLetterBar.setOverlay(overlay);
        mLetterBar.setOnLetterChangedListener(new SideLetterBars.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = mCityAdapter.getLetterPosition(letter);
                mListView.setSelection(position);
            }
        });
        mCityAdapter = new MilkAdapters(this, null, 0);
        mListView.setAdapter(mCityAdapter);
        mCityAdapter.setOnCityClickListener(new MilkAdapters.OnCityClickListener() {
            @Override
            public void onCityClick(MilkInfo name) {
                back(name);
            }

            @Override
            public void onLocateClick() {

            }
        });

    }
    private Subscription mSubscription;
    @Override
    public void bindEvents() {
        mSubscription = RxBus.getInstance().toObserverable(SubscriptionBean.RxBusSendBean.class).subscribe(new Action1<SubscriptionBean.RxBusSendBean>() {
            @Override
            public void call(SubscriptionBean.RxBusSendBean bean) {
                if (bean == null) return;
                if (SubscriptionBean.CLOSE == bean.type) {
                    finish();
                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        RxBus.getInstance().addSubscription(mSubscription);
         tvAdd.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

             }
         });

        etSearch.setOnClearEditTextListener(new ClearEditText.ClearEditTextListener() {
            @Override
            public void afterTextChanged4ClearEdit(Editable s) {
                if(TextUtil.isNotEmpty(s.toString())){
                    name=s.toString();
                    presenter.listMilkInfo(s.toString(), "1", "100");
                }
            }

            @Override
            public void changeText(CharSequence s) {

            }
        });
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                PermissionGen.with(SearchMilkActivity.this)
                        .addRequestCode(PermissionConst.REQUECT_DATE)
                        .permissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .request();
            }

            @Override
            public void navigationimg() {

            }
        });
    }

    @Override
    public void initData() {

    }





    private void back(MilkInfo city) {
        // ToastUtils.showToast(this, "点击的城市：" + city);
        closeKeyBoard();
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.MILKINFO,city.id));
        finish();
    }


    public void getConturySuccess(List<MilkInfo> prefices) {
//        if(prefices.size()!=0){
//            ShareDataManager.getInstance().Save(CityPickerActivity.this,ShareDataManager.PERFIX_DATA, new Gson().toJson(prefices));
//        }
        if (prefices != null && prefices.size() > 0) {

            mCityAdapter.setData(prefices,name);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
                if (data != null) {
                    String content = data.getStringExtra("scan_result");
                    RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.SCAN,content));
//                    finish();
                }
            }


    }

    @PermissionSuccess(requestCode = PermissionConst.REQUECT_DATE)
    public void requestSdcardSuccess() {
        Intent intent = new Intent(SearchMilkActivity.this, CaptureActivity.class);
         /*ZxingConfig是配置类  可以设置是否显示底部布局，闪光灯，相册，是否播放提示音  震动等动能
         * 也可以不传这个参数
         * 不传的话  默认都为默认不震动  其他都为true
         * */

        //ZxingConfig config = new ZxingConfig();
        //config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
        //config.setPlayBeep(true);//是否播放提示音
        //config.setShake(true);//是否震动
        //config.setShowAlbum(true);//是否显示相册
        //config.setShowFlashLight(true);//是否显示闪光灯
        //intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    private int REQUEST_CODE_SCAN = 6;




    private List<MilkInfo> milkInfo;

    @Override
    public <T> void toEntity(T entity, int type) {
        dimessProgress();
        milkInfo = ( List<MilkInfo> ) entity;
        getConturySuccess(milkInfo);

    }

    @Override
    public void toNextStep(int type) {
       dimessProgress();
    }

    @Override
    public void showTomast(String msg) {
       showToasts(msg);
        dimessProgress();
    }
}
