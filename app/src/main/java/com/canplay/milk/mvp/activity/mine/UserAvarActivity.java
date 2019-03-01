package com.canplay.milk.mvp.activity.mine;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.canplay.medical.R;
import com.canplay.milk.base.BaseActivity;
import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.bean.BASE;
import com.canplay.milk.bean.UploadFileBean;
import com.canplay.milk.mvp.activity.wiki.SendRecordActivity;
import com.canplay.milk.mvp.component.DaggerBaseComponent;
import com.canplay.milk.mvp.present.LoginContract;
import com.canplay.milk.mvp.present.LoginPresenter;
import com.canplay.milk.permission.PermissionConst;
import com.canplay.milk.permission.PermissionGen;
import com.canplay.milk.permission.PermissionSuccess;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.UploadUtil;
import com.canplay.milk.view.CircleTransform;
import com.canplay.milk.view.NavigationBar;
import com.canplay.milk.view.SquareTransform;
import com.google.gson.Gson;
import com.mykar.framework.utils.FrameworkConstant;
import com.mykar.framework.utils.Log;
import com.mykar.framework.utils.PhotoUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

/**
 * 用户头像
 */
public class UserAvarActivity extends BaseActivity implements LoginContract.View {

    @Inject
    LoginPresenter presenter;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_choose)
    TextView tvChoose;
    @BindView(R.id.tv_local)
    TextView tvLocal;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    private PhotoUtils photoUtils;

    @Override
    public void initViews() {
        setContentView(R.layout.acitvity_avator);
        ButterKnife.bind(this);

        DaggerBaseComponent.builder().appComponent(((BaseApplication) getApplication()).getAppComponent()).build().inject(this);
        presenter.attachView(this);
        mGson = new Gson();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        if (Build.VERSION.SDK_INT >= 24) {
            builder.detectFileUriExposure();
        }
        navigationBar.setNavigationBarListener(this);
        photoUtils = new PhotoUtils(this);

        Glide.with(this).load(SpUtil.getInstance().getAvator()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                bitmap = resource;
            }
        });
        Glide.with(this).load(SpUtil.getInstance().getAvator()).asBitmap().transform(new SquareTransform(this)).into(ivImg);

//        mWindowAddPhoto = new PhotoPopupWindow(this);
    }

    private Bitmap bitmap;

    @PermissionSuccess(requestCode = PermissionConst.REQUECT_CODE_CAMERA)
    public void requestSdcardSuccess() {
        Picker.from(this)
                .count(1)
                .enableCamera(true)
                .setEngine(new ImageLoaderEngine())
                .setAdd_watermark(false)
                .isScan(false)
                .forResult(REQUEST_CODE_CHOOSE);
    }

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

                String[] milks = imgs.get(0).split("milk");
                File output = new File(imgs.get(0));
                if (Build.VERSION.SDK_INT >= 24) {

                    String authority = this.getPackageName() + ".fileProvider";
                    imageUri = FileProvider.getUriForFile(this, authority, output);
                } else {
                    imageUri = Uri.fromFile(output);
                }
                startPhotoCrop();


            } else {
                if (cropImage != null) {

                    showProgress("上传中...");
                    if (TextUtil.isNotEmpty(SpUtil.getInstance().getToken())) {
                        map.put("token", SpUtil.getInstance().getToken());

                    }
                    map.put("userId", SpUtil.getInstance().getUserId());
                    maps.put("file", cropImage);
                    updateAvator(map, maps);
                    Glide.with(UserAvarActivity.this).load(cropImage).asBitmap().transform(new SquareTransform(UserAvarActivity.this)).placeholder(R.drawable.moren).into(ivImg);

                }

            }

        }
    }

    private String imgPath;
    private Gson mGson;

    private Map<String, String> map = new HashMap<>();
    private Map<String, File> maps = new HashMap<>();

    public void updateAvator(final Map<String, String> map, final Map<String, File> maps) {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String url = "";
                try {

                    url = UploadUtil.UpLoadImg(UserAvarActivity.this, "/web/updateBabyImg", map, maps);


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


                        RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.UPDATE, ""));
                        dimessProgress();

                    }
                });

    }

    private int REQUEST_CODE_CHOOSE = 2;

    @Override
    public void bindEvents() {
        tvLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (cropImgUri != null) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), cropImgUri);
                        photoUtils.saveBitmap(bitmap, UserAvarActivity.this);
                        showToasts("图片保存成功");
                    } else {
                        if (bitmap != null) {
                            photoUtils.saveBitmap(bitmap, UserAvarActivity.this);
                            showToasts("图片保存成功");
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        tvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyBoard();
                PermissionGen.with(UserAvarActivity.this)
                        .addRequestCode(PermissionConst.REQUECT_CODE_CAMERA)
                        .permissions(Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .request();
            }
        });

    }

    /**
     * 开启裁剪相片
     */
    private Uri cropImgUri;
    private Uri imageUri;
    private File cropImage;

    public void startPhotoCrop() {
        //创建file文件，用于存储剪裁后的照片
//        File cropImage = new File(Environment.getExternalStorageDirectory(), "crop_image.jpg");
        cropImage = new File(getExternalCacheDir(), "crop_image.jpg");
        try {
            if (cropImage.exists()) {
                cropImage.delete();
            }
            cropImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cropImgUri = Uri.fromFile(cropImage);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //设置源地址uri
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("scale", false);
        //设置目的地址uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImgUri);
        //设置图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG);
        intent.putExtra("return-data", false);//data不需要返回,避免图片太大异常
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, 3);
    }

    @Override
    public void initData() {

    }

    @Override
    public <T> void toEntity(T entity, int type) {
        dimessProgress();
        showToasts("上传成功");
    }

    @Override
    public void showTomast(String msg) {
        dimessProgress();

        showToasts(msg);
    }
}
