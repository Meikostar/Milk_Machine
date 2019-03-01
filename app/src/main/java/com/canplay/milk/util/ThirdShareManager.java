package com.canplay.milk.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;


import com.canplay.medical.R;
import com.canplay.milk.bean.ShareBean;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/***
 * 功能描述:分享
 * 作者:xiongning
 * 时间:2017/01/23
 * 版本:1.0
 ***/

public class ThirdShareManager {

   private static ThirdShareManager instance;

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private static final String WeChat_APP_ID="wx58abcea55d0aff13";//"wxdcb688d9027c6647";




   private static final int THUMB_SIZE = 150;

   private Context mContext;

   private ThirdShareManager(){

   }

   public static ThirdShareManager getInstance(){

       if(instance==null){
           instance=new ThirdShareManager();
       }

       return instance;
   }

    public void init(Context mContext){

        this.mContext=mContext;

        onWeChat();


    }




    /**
     * 微信
     */
    private void onWeChat(){
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(mContext, WeChat_APP_ID, true);
        // 将该app注册到微信
        api.registerApp(WeChat_APP_ID);
    }

    /**
     * 把网络资源图片转化成bitmap
     * @param url  网络资源图片
     * @return  Bitmap
     */
    public static Bitmap GetLocalOrNetBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new URL(url).openStream(), 1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void copy(InputStream in, OutputStream out) throws IOException {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        if (!(out instanceof BufferedOutputStream)) {
            out = new BufferedOutputStream(out);
        }
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
    }
    /**
     * 网页分享
     */
   public void shareWeChat(final ShareBean mShareBean, final boolean isWeChat){

       if(!isWeixinAvilible(mContext)){

           ToastUtil.showToast("分享失败，未安装微信应用");

           return;
       }
       if(mShareBean==null){

           ToastUtil.showToast("分享失败，未获取到分享信息");

           return;
       }

       Observable.create(new Observable.OnSubscribe<WXMediaMessage>() {
           @Override
           public void call(Subscriber<? super WXMediaMessage> subscriber) {
               if (TextUtil.isEmpty(mShareBean.url_))
                   mShareBean.url_ = "http://www.baidu.com";

               if (TextUtil.isEmpty(mShareBean.title_))
                   mShareBean.title_ = "奶粉机";

               if (TextUtil.isEmpty(mShareBean.content_))
                   mShareBean.content_ = "奶粉机";

               WXWebpageObject webpage = new WXWebpageObject();

               webpage.webpageUrl = mShareBean.url_;

               WXMediaMessage msg = new WXMediaMessage(webpage);

               msg.title = mShareBean.title_;

               msg.description = mShareBean.content_;

               if(TextUtil.isNotEmpty(mShareBean.img_)){

                   Bitmap thumb = Bitmap.createScaledBitmap(GetLocalOrNetBitmap(ImageUtil.getImageUrl_800(mShareBean.img_)), 120, 120, true);//压缩Bitmap

                   msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);

               }else {

                   Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_baby_logo);

                   Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);

                   bmp.recycle();

                   msg.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);

               }
               subscriber.onNext(msg);
           }
       }).subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Observer<WXMediaMessage>() {
                   @Override
                   public void onCompleted() {
                   }
                   @Override
                   public void onError(Throwable e) {

                       ToastUtil.showToast("分享失败");

                   }
                   @Override
                   public void onNext(WXMediaMessage obj) {

                       SendMessageToWX.Req req = new SendMessageToWX.Req();

                       req.transaction = buildTransaction("webpage");

                       req.message = obj;

                       if (isWeChat)
                           req.scene = SendMessageToWX.Req.WXSceneSession;
                       else
                           req.scene = SendMessageToWX.Req.WXSceneTimeline;

                       boolean isSuccess=api.sendReq(req);

                       if(!isSuccess)
                           ToastUtil.showToast("微信分享失败");
                   }
               });
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }


}
