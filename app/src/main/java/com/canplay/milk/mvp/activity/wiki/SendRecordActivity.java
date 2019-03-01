package com.canplay.milk.mvp.activity.wiki;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;

import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.BASE;
import com.canplay.milk.bean.UploadFileBean;
import com.canplay.milk.bean.WIPI;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.BaseContract;
import com.canplay.milk.mvp.present.BasesPresenter;
import com.canplay.milk.permission.PermissionConst;
import com.canplay.milk.permission.PermissionFail;
import com.canplay.milk.permission.PermissionGen;
import com.canplay.milk.permission.PermissionSuccess;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.TimeUtil;
import com.canplay.milk.util.UploadUtil;
import com.canplay.milk.view.ClearEditText;
import com.canplay.milk.view.ImageUploadView;
import com.canplay.milk.view.NavigationBar;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.valuesfeng.picker.ImageSelectActivity;
import io.valuesfeng.picker.Picker;
import io.valuesfeng.picker.widget.ImageLoaderEngine;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/***
 * 记录编辑
 */
public class SendRecordActivity extends BaseActivity implements BaseContract.View {

    @Inject
    BasesPresenter presenter;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_content)
    ClearEditText etContent;
    @BindView(R.id.iv_add_photo)
    TextView ivAddPhoto;
    @BindView(R.id.piuv_remark_image)
    ImageUploadView piuvRemarkImage;
    @BindView(R.id.tv_time)
    TextView txtTime;
    @BindView(R.id.ll_bottom_button)
    View llBottomButton;
    @BindView(R.id.tv_cout)
    TextView tvCout;


    private List<File> files = new ArrayList<>();

    private ArrayList<UploadFileBean> img_path = new ArrayList<>();

    private String content;

    private int count = 3;
    private WIPI wipi;

    @Override
    public void initViews() {
        setContentView(R.layout.activity_send_dynamic);
        ButterKnife.bind(this);
        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        mGson = new Gson();
        wipi = (WIPI) getIntent().getSerializableExtra("data");
        txtTime.setText(TimeUtil.formatTims(System.currentTimeMillis()));
        etContent.setOnClearEditTextListener(new ClearEditText.ClearEditTextListener() {
            @Override
            public void afterTextChanged4ClearEdit(Editable s) {

            }

            @Override
            public void changeText(CharSequence s) {
               if(TextUtil.isNotEmpty(s.toString())){
                   tvCout.setText(s.length()+"/200");
               }else {
                   tvCout.setText("0/200");
               }
            }
        });
    }

    @Override
    public void bindEvents() {
        piuvRemarkImage.setOnActionListener(new ImageUploadView.OnActionListener() {
            @Override
            public void onItemDelete(int position) {
                img_path.remove(position);

                notifyImageDataChange();
            }

            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onAddMoreClick() {
                if (count - img_path.size() >= 1) {
                    closeKeyBoard();
                    PermissionGen.with(SendRecordActivity.this)
                            .addRequestCode(PermissionConst.REQUECT_CODE_CAMERA)
                            .permissions(Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)
                            .request();
                }
            }

            @Override
            public void onItemPositionChange() {
                //mUploadView.notifyItemChange(0);
            }
        });


        ivAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                PermissionGen.with(SendRecordActivity.this)
                        .addRequestCode(PermissionConst.REQUECT_CODE_CAMERA)
                        .permissions(Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .request();

            }
        });
        navigationBar.setNavigationBarListener(new NavigationBar.NavigationBarListener() {
            @Override
            public void navigationLeft() {
                finish();
            }

            @Override
            public void navigationRight() {
                if (img_path.size() == 0) {
                    showToasts("请选择图片 ");
                    return;
                }
                if (TextUtil.isEmpty(etContent.getText().toString())) {
                    showToasts("说点什么吧");
                    return;
                }
                showProgress("上传中...");
                int a = 0;

                for (UploadFileBean bean : img_path) {
                    a++;
                    if (TextUtil.isEmpty(bean.getForderPath())) {

                        if (a == 1) {
                            url = bean.getUrl_();
                        } else {
                            url = url + "," + bean.getUrl_();
                        }
                        if (a == img_path.size()) {
                            content=etContent.getText().toString().trim();
                            presenter.growRecordUpdate(url, content, wipi.id);
                        }
                    } else {
                        i = a;
                        updateAvator(new File(bean.getForderPath()));
                        return;
                    }
                }
            }

            @Override
            public void navigationimg() {

            }
        });
    }

    private int poition;

    @Override
    public void initData() {
        if (wipi != null) {
            if (TextUtil.isNotEmpty(wipi.imgResourceKeys)) {
                String[] split = wipi.imgResourceKeys.split(",");
                for (int i = 0; i < split.length; i++) {
                    if (TextUtil.isNotEmpty(split[i])) {
                        UploadFileBean bean = new UploadFileBean(1);
                        bean.setUrl_(split[i]);
                        img_path.add(bean);
                    }
                }
                notifyImageDataChange();
            }
            if (TextUtil.isNotEmpty(wipi.content)) {
                etContent.setText(wipi.content);
            }
        }
    }

    @PermissionSuccess(requestCode = PermissionConst.REQUECT_CODE_CAMERA)
    public void requestSdcardSuccess() {
        Picker.from(this)
                .count(count - img_path.size())
                .enableCamera(true)
                .setEngine(new ImageLoaderEngine())
                .setAdd_watermark(false)
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @PermissionFail(requestCode = PermissionConst.REQUECT_CODE_CAMERA)
    public void requestSdcardFailed() {
//        showToast(getString(R.string.getqxdefault));
    }

    private int REQUEST_CODE_CHOOSE = 3;
    private int upload_position;

    /**
     * 删除照片和修改相册的回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE) {
                List<String> imgs = data.getStringArrayListExtra(ImageSelectActivity.EXTRA_RESULT_SELECTION);

                for (String imgPath : imgs) {
                    if (imgPath != null) {
                        UploadFileBean bean = new UploadFileBean(1);
                        bean.setForderPath(imgPath);
                        img_path.add(bean);
                    }
                }


                notifyImageDataChange();
                upload_position = 0;
            }

        }
    }

    private Map<String, String> map = new HashMap<>();
    private Map<String, File> maps = new HashMap<>();

    public void updateAvator(File file) {
        if (TextUtil.isNotEmpty(SpUtil.getInstance().getToken())) {
            map.put("token", SpUtil.getInstance().getToken());

        }
        map.put("userId", SpUtil.getInstance().getUserId());
        maps.put("file", file);
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String url = "";
                try {
                    url = UploadUtil.UpLoadImg(SendRecordActivity.this, "/web/growRecordImgUpload", map, maps);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                subscriber.onStart();
                subscriber.onNext(url);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        content=etContent.getText().toString().trim();
                        BASE info = mGson.fromJson(s, BASE.class);
                        if (info == null || info.data == null) {
                            dimessProgress();
                            return;
                        }

                        if (i == img_path.size()) {
                            if (i == 1) {
                                url = info.data;
                            } else {
                                url = url + "," + info.data;
                            }
                            if (wipi != null) {
                                presenter.growRecordUpdate(url, content, wipi.id);
                            } else {
                                presenter.growRecordInsert(url, content);
                            }

                            dimessProgress();

                        } else {
                            if (i == 1) {
                                url = info.data;
                            } else {
                                url = url + "," + info.data;
                            }

                            if (TextUtil.isEmpty(img_path.get(i).getForderPath())) {
                                url = url + "," + img_path.get(i).getUrl_();

                            } else {
                                updateAvator(new File(img_path.get(i).getForderPath()));

                            }


                            i++;
                        }


                    }
                });

    }

    private String url = "";
    private int i = 1;
    private Gson mGson;

    private void notifyImageDataChange() {
        if (img_path == null || img_path.size() == 0) {
            ivAddPhoto.setVisibility(View.VISIBLE);
            piuvRemarkImage.setVisibility(View.GONE);
        } else {
            piuvRemarkImage.setVisibility(View.VISIBLE);
            ivAddPhoto.setVisibility(View.GONE);
        }
        piuvRemarkImage.setData(img_path);
    }

    @Override
    public <T> void toEntity(T entity, int type) {
        showToasts("上传成功");
        dimessProgress();
        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.GROUP, ""));
        finish();
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
