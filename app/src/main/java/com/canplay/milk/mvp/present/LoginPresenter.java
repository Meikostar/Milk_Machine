package com.canplay.milk.mvp.present;


import android.support.annotation.NonNull;
import android.text.TextUtils;


import com.canplay.milk.base.config.NetConfig;
import com.canplay.milk.base.manager.ApiManager;
import com.canplay.milk.base.manager.AppManager;
import com.canplay.milk.bean.BASE;
import com.canplay.milk.bean.USER;
import com.canplay.milk.mvp.http.BaseApi;
import com.canplay.milk.net.MySubscriber;
import com.canplay.milk.util.CryptUtil;
import com.canplay.milk.util.SpUtil;
import com.canplay.milk.util.StringUtil;
import com.canplay.milk.util.TextUtil;
import com.canplay.milk.util.TimeUtil;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.inject.Inject;

import retrofit2.http.QueryMap;
import rx.Observable;
import rx.Subscription;


public class LoginPresenter implements LoginContract.Presenter {
    private Subscription subscription;

    private LoginContract.View mView;

    private BaseApi contactApi;

    @Inject
    LoginPresenter(ApiManager apiManager){
        contactApi = apiManager.createApi(BaseApi.class);
    }
    @Override
    public void goLogin(String mobile, String pwd) {
        Map<String, String> params = new TreeMap<>();
        params.put("mobile", mobile);
        params.put("pwd", StringUtil.md5(pwd));
        subscription = ApiManager.setSubscribe(contactApi.Login(ApiManager.getParameters(params, true)), new MySubscriber<USER>(){
            @Override
            public void onError(Throwable e){
                super.onError(e);
                mView.showTomast(e.getMessage());

            }

            @Override
            public void onNext(USER entity){
                mView.toEntity(entity,0);
                SpUtil.getInstance().putUser(entity);
            }
        });
    }
    @Override
    public void resetPwd(String mobile, String resetCode, String pwd) {
        Map<String, String> params = new TreeMap<>();
        params.put("mobile", mobile);
        params.put("resetCode", resetCode);
        params.put("pwd", StringUtil.md5(pwd));
        params.put("rePwd", StringUtil.md5(pwd));
        subscription = ApiManager.setSubscribe(contactApi.resetPwd(ApiManager.getParameters(params, true)), new MySubscriber<USER>(){
            @Override
            public void onError(Throwable e){
                super.onError(e);

                mView.showTomast(e.getMessage());


            }

            @Override
            public void onNext(USER entity){
                mView.toEntity(entity,2);

//                SpUtil.getInstance().putString(SpUtil.USER_ID,entity.merchantId);
            }
        });
    }
    @Override
    public void getLastestVersion() {
        Map<String, String> params = new TreeMap<>();
        params.put("platformType", "Android");
        subscription = ApiManager.setSubscribe(contactApi.getLastestVersion(ApiManager.getParameters(params, true)), new MySubscriber<BASE>(){
            @Override
            public void onError(Throwable e){
                super.onError(e);

                mView.showTomast(e.getMessage());


            }

            @Override
            public void onNext(BASE entity){
                mView.toEntity(entity,2);

//                SpUtil.getInstance().putString(SpUtil.USER_ID,entity.merchantId);
            }
        });
    }

    @Override
    public void mobileRegister(String mobile, String regCode, String pwd,
                               String birthDate, String name, String sex, String weight) {
        Map<String, String> params = new TreeMap<>();
        params.put("mobile", mobile);
        params.put("regCode", regCode);
        params.put("pwd", pwd);
        params.put("rePwd", pwd);
        params.put("birthDate", birthDate);
        params.put("name", name);
        params.put("sex", sex);
        params.put("weight", weight);
        subscription = ApiManager.setSubscribe(contactApi.mobileRegister(ApiManager.getParameters(params, true)), new MySubscriber<USER>(){
            @Override
            public void onError(Throwable e){
                super.onError(e);

                mView.showTomast(e.getMessage());


            }

            @Override
            public void onNext(USER entity){
                mView.toEntity(entity,0);
                SpUtil.getInstance().putUser(entity);
//                SpUtil.getInstance().putString(SpUtil.USER_ID,entity.merchantId);
            }
        });
    }

    @Override
    public void updateMyBaseInfo(String name, String fatherName, String motherName) {
        Map<String, String> params = new TreeMap<>();
        USER users = SpUtil.getInstance().getUsers();
        params.put("name", name);
        params.put("sex", users.sex);
        params.put("birthdayDate", TimeUtil.formatToName(Long.valueOf(users.birthday)));
        params.put("weight", users.weight);
        params.put("fatherName", fatherName);
        params.put("motherName", motherName);
        subscription = ApiManager.setSubscribe(contactApi.updateMyBaseInfo(ApiManager.getParameters(params, true)), new MySubscriber<USER>(){
            @Override
            public void onError(Throwable e){
                super.onError(e);

                mView.showTomast(e.getMessage());


            }

            @Override
            public void onNext(USER entity){
                mView.toEntity(entity,11);
                SpUtil.getInstance().putUser(entity);
//                SpUtil.getInstance().putString(SpUtil.USER_ID,entity.merchantId);
            }
        });
    }



    @Override
    public void updatePwd(String oldPassword, String pwd, String rePwd) {
        Map<String, String> params = new TreeMap<>();

        params.put("oldPassword",StringUtil.md5(oldPassword));
        params.put("pwd", StringUtil.md5(pwd));
        params.put("rePwd", StringUtil.md5(rePwd));

        subscription = ApiManager.setSubscribe(contactApi.updatePwd(ApiManager.getParameters(params, true)), new MySubscriber<USER>(){
            @Override
            public void onError(Throwable e){
                super.onError(e);

                mView.showTomast(e.getMessage());


            }

            @Override
            public void onNext(USER entity){
//                SpUtil.getInstance().putUser(entity);
                mView.toEntity(entity,11);

//                SpUtil.getInstance().putString(SpUtil.USER_ID,entity.merchantId);
            }
        });
    }
    @Override
    public void EditorMyBaseInfo(String name, String weight, String birthday) {
        Map<String, String> params = new TreeMap<>();
        USER users = SpUtil.getInstance().getUsers();
        params.put("name",name);
        params.put("sex", users.sex);
        params.put("birthdayDate", birthday);
        params.put("weight", weight);
        params.put("fatherName",  users.fatherName);
        params.put("motherName",  users.motherName);
        subscription = ApiManager.setSubscribe(contactApi.updateMyBaseInfo(ApiManager.getParameters(params, true)), new MySubscriber<USER>(){
            @Override
            public void onError(Throwable e){
                super.onError(e);

                mView.showTomast(e.getMessage());


            }

            @Override
            public void onNext(USER entity){
                SpUtil.getInstance().putUser(entity);
                mView.toEntity(entity,11);

//                SpUtil.getInstance().putString(SpUtil.USER_ID,entity.merchantId);
            }
        });
    }
    @Override
    public void downApk() {

        Map<String, String> params = new TreeMap<>();
        subscription = ApiManager.setSubscribe(contactApi.downApk(ApiManager.getParameters(params, true)), new MySubscriber<BASE >() {
            @Override
            public void onNext(BASE ruslt) {

                mView.toEntity(ruslt,0);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }


    @Override
    public void getMyBaseInfo() {

        Map<String, String> params = new TreeMap<>();


        subscription = ApiManager.setSubscribe(contactApi.getMyBaseInfo(ApiManager.getParameters(params, true)), new MySubscriber<USER >() {
            @Override
            public void onNext(USER ruslt) {

                mView.toEntity(ruslt,0);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    @Override
    public void getCode(String phone) {

        Map<String, String> params = new TreeMap<>();
        params.put("mobile", phone);
        subscription = ApiManager.setSubscribe(contactApi.getCode(params), new MySubscriber<BASE >() {
            @Override
            public void onNext(BASE ruslt) {

                mView.toEntity(ruslt,1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    public void checkCode(String mobile,String code,String pwd) {

        Map<String, String> params = new TreeMap<>();
        params.put("mobile", mobile);
        params.put("regCode", code);
        params.put("pwd", pwd);
        params.put("rePwd", pwd);
        subscription = ApiManager.setSubscribe(contactApi.checkCode(ApiManager.getParameters(params, true)), new MySubscriber<BASE>() {
            @Override
            public void onNext(BASE ruslt) {

                mView.toEntity(ruslt,2);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }


    @Override
    public void getForgetPswCode(String phone) {

        Map<String, String> params = new TreeMap<>();
        params.put("mobile", phone);
        subscription = ApiManager.setSubscribe(contactApi.getForgetPswCode(ApiManager.getParameters(params, true)), new MySubscriber<BASE >() {
            @Override
            public void onNext(BASE ruslt) {

                mView.toEntity(ruslt,1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    /*
   计算密钥
    */
    private static String getSign(Map<String, Object> parametersMap){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Object> e : parametersMap.entrySet()){
            if(TextUtils.isEmpty((String) e.getValue())){
                continue;
            }
            String value = e.getValue().toString().trim();
            String[] valueArr = value.split(String.valueOf(NetConfig.CHARKEY));
            if(valueArr.length <= 1){
                sb.append(e.getKey());
                sb.append("=");
                sb.append(value);
                sb.append("&");
            }else{
                StringBuilder str = new StringBuilder();
                for(String c : valueArr){
                    str.append(c);
                    str.append(String.valueOf(NetConfig.CHARKEY));
                }
                str.delete(str.length() - 1, str.length());
                sb.append(e.getKey());
                sb.append("=");
                sb.append(str.toString());
                sb.append("&");
            }
        }
        int len = sb.length();
        sb.delete(len - 1, len);
        try{
            return CryptUtil.md5(sb.toString() + NetConfig.APP_KEY).toLowerCase().substring(NetConfig.startNum, NetConfig.endNum);
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }
    @Override
    public void updateBabyImg(File file) {

        Map<String, Object> parametersMap = new TreeMap<>();
        if(TextUtil.isNotEmpty(SpUtil.getInstance().getToken())){

                //把用户的userID和token带上
                parametersMap.put("device", AppManager.imei);//设备标识
                parametersMap.put("times", System.currentTimeMillis() + "");

            if(TextUtil.isNotEmpty(SpUtil.getInstance().getToken())){
                parametersMap.put("token", SpUtil.getInstance().getToken());

            }
            parametersMap.put("userId", SpUtil.getInstance().getUserId());
            parametersMap.put("platform", "1"); //手表
            parametersMap.put("version", AppManager.info.versionCode + "");//客户端版本
            parametersMap.put("sign", getSign(parametersMap));

            parametersMap.put("file", file);
        }

        subscription = ApiManager.setSubscribe(contactApi.updateBabyImg(parametersMap), new MySubscriber<BASE >() {
            @Override
            public void onNext(BASE ruslt) {

                mView.toEntity(ruslt,0);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void growRecordImgUpload(File file) {

        Map<String, Object> parametersMap = new TreeMap<>();
        if(TextUtil.isNotEmpty(SpUtil.getInstance().getToken())){
            //把用户的userID和token带上
            parametersMap.put("device", AppManager.imei);//设备标识
            parametersMap.put("times", System.currentTimeMillis() + "");
            if(TextUtil.isNotEmpty(SpUtil.getInstance().getToken())){
                parametersMap.put("token", SpUtil.getInstance().getToken());

            }
            parametersMap.put("userId", SpUtil.getInstance().getUserId());
            parametersMap.put("platform", "1"); //手表
            parametersMap.put("version", AppManager.info.versionCode + "");//客户端版本
            parametersMap.put("sign", getSign(parametersMap));
            parametersMap.put("file", file);
        }

        subscription = ApiManager.setSubscribe(contactApi.updateBabyImg(parametersMap), new MySubscriber<BASE >() {
            @Override
            public void onNext(BASE ruslt) {

                mView.toEntity(ruslt,0);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    @Override
    public void logout() {

        Map<String, String> params = new TreeMap<>();
        subscription = ApiManager.setSubscribe(contactApi.logout(ApiManager.getParameters(params, true)), new MySubscriber<BASE >() {
            @Override
            public void onNext(BASE ruslt) {

                mView.toEntity(ruslt,6);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    @Override
    public void attachView(@NonNull LoginContract.View view){
        mView = view;
    }


    @Override
    public void detachView(){
        if(subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
        mView = null;
    }
}
