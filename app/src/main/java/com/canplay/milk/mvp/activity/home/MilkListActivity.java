package com.canplay.milk.mvp.activity.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
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
import com.canplay.milk.bean.Milk;
import com.canplay.milk.bean.MilkInfo;
import com.canplay.milk.mvp.adapter.MilkAdapter;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.permission.PermissionConst;
import com.canplay.milk.permission.PermissionGen;
import com.canplay.milk.permission.PermissionSuccess;
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
public class MilkListActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;

    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.listview_all_city)
    ListView mListView;
    @BindView(R.id.tv_letter_overlay)
    TextView overlay;
    @BindView(R.id.side_letter_bars)
    SideLetterBars mLetterBar;
    @BindView(R.id.tv_add)
    TextView tvAdd;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;

    public static final String KEY_PICKED_CITY = "picked_milk";
    @BindView(R.id.tv_search)
    TextView tvSearch;

    //  private ListView mResultListView;

    private EditText searchBox;
    private ImageView clearBtn;

    private ViewGroup emptyView;
    private MilkAdapter mCityAdapter;


    @Override
    public void initViews() {
        setContentView(R.layout.activity_city_list);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        showProgress("加载中...");
        presenter.getListMilkCategory();
        mLetterBar = (SideLetterBars) findViewById(R.id.side_letter_bars);
        mLetterBar.setOverlay(overlay);
        mLetterBar.setOnLetterChangedListener(new SideLetterBars.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = mCityAdapter.getLetterPosition(letter);
                mListView.setSelection(position);
            }
        });
        mCityAdapter = new MilkAdapter(this, null, 0);
        mListView.setAdapter(mCityAdapter);
        mCityAdapter.setOnCityClickListener(new MilkAdapter.OnCityClickListener() {
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
                if (SubscriptionBean.REFESH_MLIK == bean.type) {
                    presenter.getListMilkCategory();
                } else if ( SubscriptionBean.MILKINFO == bean.type) {
                    finish();
                }else if  (SubscriptionBean.CLOSE == bean.type) {
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
                startActivity(new Intent(MilkListActivity.this, AddBrandsActivity.class));
            }
        });

        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                PermissionGen.with(MilkListActivity.this)
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

        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MilkListActivity.this, SearchMilkActivity.class));
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra("scan_result");
                RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.SCAN, content));
//                finish();
//                    showToasts("扫描结果为：" + content);
//                result.setText("扫描结果为：" + content);
            }
        }


    }

    @PermissionSuccess(requestCode = PermissionConst.REQUECT_DATE)
    public void requestSdcardSuccess() {
        Intent intent = new Intent(MilkListActivity.this, CaptureActivity.class);
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

    private List<Milk> list;


    private void back(MilkInfo city) {
        // ToastUtils.showToast(this, "点击的城市：" + city);
        Intent data = new Intent(MilkListActivity.this, SearchMilkActivity.class);
        data.putExtra("name", city.name);
        startActivity(data);

    }


    public void getConturySuccess(List<MilkInfo> prefices) {
//        if(prefices.size()!=0){
//            ShareDataManager.getInstance().Save(CityPickerActivity.this,ShareDataManager.PERFIX_DATA, new Gson().toJson(prefices));
//        }
        if (prefices != null && prefices.size() > 0) {
//            emptyView.setVisibility(View.GONE);
            mCityAdapter.setData(prefices);
        } else {
//            emptyView.setVisibility(View.VISIBLE);
        }


    }


    private List<MilkInfo> milkInfo;

    @Override
    public <T> void toEntity(T entity, int type) {
        dimessProgress();
        milkInfo = (List<MilkInfo>) entity;
        getConturySuccess(milkInfo);
    }

    @Override
    public void toNextStep(int type) {
        dimessProgress();
    }

    @Override
    public void showTomast(String msg) {
        dimessProgress();
        showToasts(msg);
    }


}
