package com.canplay.milk.mvp.present;

import com.canplay.milk.base.BasePresenter;
import com.canplay.milk.base.BaseView;

public class BaseContract {
    public    interface View extends BaseView {

//        <T> void toList(List<T> list, int type, int... refreshType);
        <T> void toEntity(T entity, int type);

        void toNextStep(int type);

        void showTomast(String msg);
    }

    public  interface Presenter extends BasePresenter<View> {

        void    getHomeInfo();
        void  getRecommendArticle();

        void updateUserVaccineStatus(String userVaccineId,String status);
        /**
         * 获取下一次疫苗时间
         */
        void getNextUserVaccineInfo();
        /**
         * 获取百科列表
         * @param type
         * @param from
         * @param take
         */
        void getArticleList(final int  type, String from, String take);

        /**
         * 上传成长记录
         * @param url
         * @param content
         */
        void growRecordInsert(String url,String content);

        /**
         * 成长记录
         */
       void growRecordList(final int  type, String from, String take);


        /**
         * 跟新成长记录
         */
        void growRecordUpdate(String imgResourceKeys,String content,String growRecordId);


        /**
         * 搜索文章
         */
        void SearchArticleList(final int  type, String from, String take,String content);

        void getListMilkCategory();
        /**
         * 记录详情
         */
        void growRecordDetail( String id);

        /**
         * 删除记录
         */
        void growRecordDelete( String id);
        /**
         * 疫苗助手列表
         */
        void getUserVaccineList();
        /**
         * 上报奶量
         */
        void insertUserMilkRecord(String waterQuantity);
        /**
         * 三日喂奶记录
         */
        void getUserMilkRecord();
        /**
         * 获取冲奶设置
         */
        void getUserMilkConf();
        /**
         * 设置冲奶设置
         */
        void setUserMilkConf(String consistence ,String waterQuantity	,String waterTemperature);
        /**
         * 新增提醒
         */
        void insertRemind(String name,String remindTime,String repeatDay);
        /**
         *  提醒列表
         */
        void getReminds();

        /**
         *  奶粉列表
         */
        void listMilkInfo(String searchName,String page,String pageSize);
        /**
         *  删除提醒状态
         */
        void deleteRemind(String id);

        /**
         *    扫码获取奶粉信息
         */
        void getMilkInfoByBarCode(String id);

        /**
         *   获取奶粉信息
         */
        void getMilkInfo(String id);
        /**
         *   更换奶粉信息
         */
        void SureMilkInfo(String id);
        /**
         *  修改提醒
         */
        void updateRemind(String id,String name,String remindTime,String repeatDay);
        /**
         * 设置提醒状态
         */
        void updateRemindStatus(String id,String validStatus);
        /**
         * 设置提醒总开关
         */
        void updateUserRemindStatus(String validStatus,boolean isCheck);
        /**
         *  用户添加奶粉信息
         */
        void createUserMilk(String grade,  String milkWeight,String waterQuantity,  String waterTemperature,  String name);
        /**
         *  设置默认冲泡的奶粉
         */
        void setUserMilk(String milkInfoId);

        /**
         *  更改疫苗提醒开关状态
         */
        void setUserVaccineRemindStatus(String vaccineRemindStatus);
        /**
         *  设置疫苗时间
         */
        void setUserVaccineTime(String userVaccineId,String vaccineTime);
    }
}
