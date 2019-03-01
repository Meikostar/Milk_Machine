package com.canplay.milk.mvp.present;


import android.support.annotation.NonNull;


import com.canplay.milk.base.manager.ApiManager;
import com.canplay.milk.bean.Article;
import com.canplay.milk.bean.BASE;
import com.canplay.milk.bean.HOME;
import com.canplay.milk.bean.MilkInfo;
import com.canplay.milk.bean.Remind;
import com.canplay.milk.bean.SetMilk;
import com.canplay.milk.bean.Vaccines;
import com.canplay.milk.bean.WIPI;
import com.canplay.milk.mvp.http.BaseApi;
import com.canplay.milk.net.MySubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import rx.Subscription;


public class BasesPresenter implements BaseContract.Presenter {
    private Subscription subscription;

    private BaseContract.View mView;

    private BaseApi contactApi;

    @Override
    public void attachView(@NonNull BaseContract.View view) {
        mView = view;
    }
    @Inject
    BasesPresenter(ApiManager apiManager){
        contactApi = apiManager.createApi(BaseApi.class);
    }

    @Override
    public void setUserMilk(String milkInfoId) {

        Map<String, String> params = new TreeMap<>();
        params.put("milkInfoId",milkInfoId);
        subscription = ApiManager.setSubscribe(contactApi.setUserMilk(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

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
    public void setUserVaccineRemindStatus(String vaccineRemindStatus) {

        Map<String, String> params = new TreeMap<>();
        params.put("vaccineRemindStatus",vaccineRemindStatus);
        subscription = ApiManager.setSubscribe(contactApi.setUserVaccineRemindStatus(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,3);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void getNextUserVaccineInfo() {

        Map<String, String> params = new TreeMap<>();
        subscription = ApiManager.setSubscribe(contactApi.getNextUserVaccineInfo(ApiManager.getParameters(params, true)), new MySubscriber<BASE>() {
            @Override
            public void onNext(BASE ruslt) {

                mView.toEntity(ruslt,7);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    @Override
    public void setUserVaccineTime(String userVaccineId,String vaccineTime) {

        Map<String, String> params = new TreeMap<>();
        params.put("userVaccineId",userVaccineId);
        params.put("vaccineTime",vaccineTime);
        subscription = ApiManager.setSubscribe(contactApi.setUserVaccineTime(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

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
    public void updateUserRemindStatus(String validStatus,final boolean isCheck) {

        Map<String, String> params = new TreeMap<>();
        params.put("validStatus",validStatus);
        subscription = ApiManager.setSubscribe(contactApi.updateUserRemindStatus(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,isCheck?8:9);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    @Override
    public void deleteRemind(String id) {

        Map<String, String> params = new TreeMap<>();
        params.put("id",id);
        subscription = ApiManager.setSubscribe(contactApi.deleteRemind(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

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
    public void getMilkInfoByBarCode(String barCode) {
        Map<String, String> params = new TreeMap<>();
        params.put("barCode",barCode);
        subscription = ApiManager.setSubscribe(contactApi.getMilkInfoByBarCode(ApiManager.getParameters(params, true)), new MySubscriber<MilkInfo>() {
            @Override
            public void onNext(MilkInfo ruslt) {

                mView.toEntity(ruslt,6);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast("未找到该产品信息");
            }
        });
    }

    @Override
    public void  getRecommendArticle() {
        Map<String, String> params = new TreeMap<>();
        subscription = ApiManager.setSubscribe(contactApi.getRecommendArticle(ApiManager.getParameters(params, true)), new MySubscriber<Article>() {
            @Override
            public void onNext(Article ruslt) {

                mView.toEntity(ruslt,5);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void    getHomeInfo() {
        Map<String, String> params = new TreeMap<>();
        subscription = ApiManager.setSubscribe(contactApi.getHomeInfo(ApiManager.getParameters(params, true)), new MySubscriber<HOME>() {
            @Override
            public void onNext(HOME ruslt) {

                mView.toEntity(ruslt,5);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);

            }
        });
    }
    @Override
    public void getMilkInfo(String id) {
        Map<String, String> params = new TreeMap<>();
        params.put("id",id);
        subscription = ApiManager.setSubscribe(contactApi.getMilkInfo(ApiManager.getParameters(params, true)), new MySubscriber<MilkInfo>() {
            @Override
            public void onNext(MilkInfo ruslt) {

                mView.toEntity(ruslt,8);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void getListMilkCategory() {
        Map<String, String> params = new TreeMap<>();
        subscription = ApiManager.setSubscribe(contactApi.getListMilkCategory(ApiManager.getParameters(params, true)), new MySubscriber<List<MilkInfo>>() {
            @Override
            public void onNext(List<MilkInfo> ruslt) {

                mView.toEntity(ruslt,8);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    @Override
    public void SureMilkInfo(String id) {
        Map<String, String> params = new TreeMap<>();
        params.put("id",id);
        subscription = ApiManager.setSubscribe(contactApi.SureMilkInfo(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,8);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void getReminds() {

        Map<String, String> params = new TreeMap<>();

        subscription = ApiManager.setSubscribe(contactApi.getReminds(ApiManager.getParameters(params, true)), new MySubscriber<Remind>() {
            @Override
            public void onNext(Remind ruslt) {

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
    public void listMilkInfo(String searchName,String page,String pageSize) {
        Map<String, String> params = new TreeMap<>();
        params.put("searchName",searchName);
        params.put("page",page);
        params.put("pageSize",pageSize);
        subscription = ApiManager.setSubscribe(contactApi.listMilkInfo(ApiManager.getParameters(params, true)), new MySubscriber<List<MilkInfo> >() {
            @Override
            public void onNext(List<MilkInfo> ruslt) {

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
    public void createUserMilk(String grade,  String milkWeight,String waterQuantity,  String waterTemperature,  String name) {

        Map<String, String> params = new TreeMap<>();
        params.put("grade",grade);
        params.put("milkWeight",milkWeight);
        params.put("waterQuantity",waterQuantity);
        params.put("waterTemperature",waterTemperature);
        params.put("name",name);
        subscription = ApiManager.setSubscribe(contactApi.createUserMilk(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

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
    public void updateRemindStatus(String id, final String validStatus) {

        Map<String, String> params = new TreeMap<>();
        params.put("id",id);
        params.put("validStatus",validStatus);
        subscription = ApiManager.setSubscribe(contactApi.updateRemindStatus(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,Integer.valueOf(validStatus)==0?2:1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void updateRemind(String id,String name,String remindTime,String repeatDay) {

        Map<String, String> params = new TreeMap<>();
        params.put("id",id);
        params.put("name",name);
        params.put("remindTime",remindTime);
        params.put("repeatDay",repeatDay);
        subscription = ApiManager.setSubscribe(contactApi.updateRemind(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

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
    public void insertRemind(String name,String remindTime,String repeatDay) {

        Map<String, String> params = new TreeMap<>();
        params.put("name",name);
        params.put("remindTime",remindTime);
        params.put("repeatDay",repeatDay);
        subscription = ApiManager.setSubscribe(contactApi.insertRemind(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void getUserMilkConf() {

        Map<String, String> params = new TreeMap<>();
        subscription = ApiManager.setSubscribe(contactApi.getUserMilkConf(ApiManager.getParameters(params, true)), new MySubscriber<SetMilk>() {
            @Override
            public void onNext(SetMilk ruslt) {

                mView.toEntity(ruslt,1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void setUserMilkConf(String consistence ,String waterQuantity	,String waterTemperature) {

        Map<String, String> params = new TreeMap<>();
        params.put("consistence",consistence);
        params.put("waterQuantity",waterQuantity);
        params.put("waterTemperature",waterTemperature);
        subscription = ApiManager.setSubscribe(contactApi.setUserMilkConf(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void insertUserMilkRecord(String waterQuantity) {

        Map<String, String> params = new TreeMap<>();
        params.put("waterQuantity",waterQuantity);
        subscription = ApiManager.setSubscribe(contactApi.insertUserMilkRecord(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,11);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }


    @Override
    public void getUserMilkRecord() {

        Map<String, String> params = new TreeMap<>();

        subscription = ApiManager.setSubscribe(contactApi.getUserMilkRecord(ApiManager.getParameters(params, true)), new MySubscriber<List<BASE>>() {
            @Override
            public void onNext(List<BASE> ruslt) {

                mView.toEntity(ruslt,1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void getUserVaccineList() {

        Map<String, String> params = new TreeMap<>();

        subscription = ApiManager.setSubscribe(contactApi.getUserVaccineList(ApiManager.getParameters(params, true)), new MySubscriber<List<Vaccines>>() {
            @Override
            public void onNext(List<Vaccines> ruslt) {

                mView.toEntity(ruslt,1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void getArticleList(final int  type, String from, String take) {

        Map<String, String> params = new TreeMap<>();
        params.put("page",from);
        params.put("pageSize",take);
        subscription = ApiManager.setSubscribe(contactApi.getArticleList(ApiManager.getParameters(params, true)), new MySubscriber<WIPI>() {
            @Override
            public void onNext(WIPI ruslt) {

                mView.toEntity(ruslt,type);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    public void SearchArticleList(final int  type, String from, String take,String content) {

        Map<String, String> params = new TreeMap<>();
        params.put("searchTitle",content);
        params.put("page",from);
        params.put("pageSize",take);
        subscription = ApiManager.setSubscribe(contactApi.getArticleList(ApiManager.getParameters(params, true)), new MySubscriber<WIPI>() {
            @Override
            public void onNext(WIPI ruslt) {

                mView.toEntity(ruslt,type);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void growRecordList(final int  type, String from, String take) {

        Map<String, String> params = new TreeMap<>();
        params.put("platformType","Android");
        params.put("page",from);
        params.put("pageSize",take);
        subscription = ApiManager.setSubscribe(contactApi.growRecordList(ApiManager.getParameters(params, true)), new MySubscriber<WIPI>() {
            @Override
            public void onNext(WIPI ruslt) {

                mView.toEntity(ruslt,type);

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }
    @Override
    public void growRecordDetail( String id) {

        Map<String, String> params = new TreeMap<>();
        params.put("growRecordId",id);
        subscription = ApiManager.setSubscribe(contactApi.growRecordDetail(ApiManager.getParameters(params, true)), new MySubscriber<WIPI>() {
            @Override
            public void onNext(WIPI ruslt) {
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
    public void growRecordDelete( String id) {

        Map<String, String> params = new TreeMap<>();
        params.put("growRecordId",id);
        subscription = ApiManager.setSubscribe(contactApi.growRecordDelete(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,8);

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }


    @Override
    public void updateUserVaccineStatus(String userVaccineId,String status) {

        Map<String, String> params = new TreeMap<>();
        params.put("userVaccineId",userVaccineId);
        params.put("status",status);
        subscription = ApiManager.setSubscribe(contactApi.updateUserVaccineStatus(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,14);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    @Override
    public void growRecordInsert(String url,String content) {

        Map<String, String> params = new TreeMap<>();
        params.put("content",content);
        params.put("imgResourceKeys",url);
        subscription = ApiManager.setSubscribe(contactApi.growRecordInsert(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    @Override
    public void growRecordUpdate(String imgResourceKeys,String content,String growRecordId) {

        Map<String, String> params = new TreeMap<>();
        params.put("content",content);
        params.put("growRecordId",growRecordId);
        params.put("imgResourceKeys",imgResourceKeys);
        subscription = ApiManager.setSubscribe(contactApi.growRecordUpdate(ApiManager.getParameters(params, true)), new MySubscriber<String>() {
            @Override
            public void onNext(String ruslt) {

                mView.toEntity(ruslt,1);

            }

            @Override
            public void onError(Throwable e) {

                super.onError(e);
                mView.showTomast(e.getMessage());
            }
        });
    }

    @Override
    public void detachView(){
        if(subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
        mView = null;
    }
}
