package com.canplay.milk.mvp.http;



import com.canplay.milk.bean.Article;
import com.canplay.milk.bean.BASE;
import com.canplay.milk.bean.HOME;
import com.canplay.milk.bean.Milk;
import com.canplay.milk.bean.MilkInfo;
import com.canplay.milk.bean.Remind;
import com.canplay.milk.bean.SetMilk;
import com.canplay.milk.bean.USER;
import com.canplay.milk.bean.Vaccines;
import com.canplay.milk.bean.WIPI;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;


public interface BaseApi {


    /**
     * Login
     * @param options
     * @return
     */


    @POST("web/mobileLogin")
    Observable<USER> Login(@QueryMap Map<String, String> options);


    /**
     * Login
     * @param options
     * @return
     */


    @POST("web/getLastestVersion")
    Observable<BASE> getLastestVersion(@QueryMap Map<String, String> options);


    /**
     * 重置密码
     * @param options
     * @return
     */

    @POST("web/resetPwd")
    Observable<USER> resetPwd(@QueryMap Map<String, String> options);

    /**
     * 下載apk
     * @param options
     * @return
     */
    @POST("Flow/Token")
    Observable<BASE> downApk(@QueryMap Map<String, String> options);
    /**
     * 获取验证码
     * @param options
     * @return
     */

    @POST("web/getRegisterCode")
    Observable<BASE> getCode(@QueryMap Map<String, String> options);

    /**
     * 校验验证码
     * @param options
     * @return
     */

    @POST("web/validateRegisterCode")
    Observable<BASE> checkCode(@QueryMap Map<String, String> options);
    /**
     * 重置密码
     * @param options
     * @return
     */

    @POST("web/getResetCode")
    Observable<BASE> getForgetPswCode(@QueryMap Map<String, String> options);

    /**
     * 登出
     * @param options
     * @return
     */

    @POST("web/logout")
    Observable<BASE> logout(@QueryMap Map<String, String> options);

    /**
     * 往期百科列表
     * @param options
     * @return
     */

    @POST("web/getArticleList")
    Observable<WIPI> getArticleList(@QueryMap Map<String, String> options);


    /**
     * 疫苗助手列表
     * @param options
     * @return
     */

    @POST("web/listUserVaccineList")
    Observable<List<Vaccines>> getUserVaccineList(@QueryMap Map<String, String> options);

    /**
     * 疫苗助手列表
     * @param options
     * @return
     */

    @POST("web/insertUserMilkRecord")
    Observable<String> insertUserMilkRecord(@QueryMap Map<String, String> options);

    /**
     * 疫苗助手列表
     * @param options
     * @return
     */

    @POST("web/listUserMilkRecord")
    Observable<List<BASE>> getUserMilkRecord(@QueryMap Map<String, String> options);

    /**
     * 疫苗助手列表
     * @param options
     * @return
     */

    @POST("web/setUserMilkConf")
    Observable<String> setUserMilkConf(@QueryMap Map<String, String> options);

    /**
     * 成长记录列表
     * @param options
     * @return
     */

    @POST("web/growRecordList")
    Observable<WIPI> growRecordList(@QueryMap Map<String, String> options);

    /**
     * 成长详情
     * @param options
     * @return
     */

    @POST("web/growRecordDetail")
    Observable<WIPI> growRecordDetail(@QueryMap Map<String, String> options);

    /**
     * 成长详情
     * @param options
     * @return
     */

    @POST("web/growRecordDelete")
    Observable<String> growRecordDelete(@QueryMap Map<String, String> options);

    /**
     * 设置冲奶设置
     * @param options
     * @return
     */

    @POST("web/getUserMilkConf")
    Observable<SetMilk> getUserMilkConf(@QueryMap Map<String, String> options);
    /**
     * 新增提醒
     * @param options
     * @return
     */

    @POST("web/insertRemind")
    Observable<String> insertRemind(@QueryMap Map<String, String> options);

    /**
     * 提醒列表
     * @param options
     * @return
     */

    @POST("web/listReminds")
    Observable<Remind> getReminds(@QueryMap Map<String, String> options);

    /**
     * 修改提醒名称
     * @param options
     * @return
     */

    @POST("web/updateRemindName")
    Observable<String> updateRemindName(@QueryMap Map<String, String> options);


    /**
     * 修改提醒
     * @param options
     * @return
     */

    @POST("web/updateRemind")
    Observable<String> updateRemind(@QueryMap Map<String, String> options);

    /**
     * 设置提醒状态
     * @param options
     * @return
     */

    @POST("web/deleteRemind")
    Observable<String> deleteRemind(@QueryMap Map<String, String> options);
    /**
     * 获取奶粉信息
     * @param options
     * @return
     */

    @POST("web/getMilkInfo")
    Observable<MilkInfo> getMilkInfo(@QueryMap Map<String, String> options);
    /**
     * 获取奶粉信息
     * @param options
     * @return
     */

    @POST("web/nice")
    Observable<HOME> getHomeInfo(@QueryMap Map<String, String> options);
    /**
     * 获取奶粉信息
     * @param options
     * @return
     */

    @POST("web/getRecommendArticle")
    Observable<Article> getRecommendArticle(@QueryMap Map<String, String> options);

    /**
     * 获取奶粉信息
     * @param options
     * @return
     */

    @POST("web/getMilkInfo")
    Observable<String> SureMilkInfo(@QueryMap Map<String, String> options);

    /**
     * 扫码获取奶粉信息
     * @param options
     * @return
     */

    @POST("web/getMilkInfoByBarCode")
    Observable<MilkInfo> getMilkInfoByBarCode(@QueryMap Map<String, String> options);
    /**
     *  奶粉列表
     * @param options
     * @return
     */

    @POST("web/listMilkInfo")
    Observable<List<MilkInfo> > listMilkInfo(@QueryMap Map<String, String> options);

    /**
     * 设置提醒总开关
     * @param options
     * @return
     */

    @POST("web/updateUserRemindStatus")
    Observable<String> updateUserRemindStatus(@QueryMap Map<String, String> options);

    /**
     * 用户添加奶粉信息
     * @param options
     * @return
     */

    @POST("web/createUserMilk")
    Observable<String> createUserMilk(@QueryMap Map<String, String> options);

    /**
     * 修改提醒
     * @param options
     * @return
     */

    @POST("web/updateRemindStatus")
    Observable<String> updateRemindStatus(@QueryMap Map<String, String> options);
    /**
     * 成长记录
     * @param options
     * @return
     */

    @POST("web/growRecordInsert")
    Observable<String> growRecordInsert(@QueryMap Map<String, String> options);
    /**
     * 成长记录
     * @param options
     * @return
     */

    @POST("web/growRecordUpdate")
    Observable<String> growRecordUpdate(@QueryMap Map<String, String> options);



    /**
     * 更新头像
     * @param options
     * @return
     */

    @POST("web/updateBabyImg")
    Observable<BASE> updateBabyImg(@QueryMap Map<String, Object> options);
    /**
     * 更新头像
     * @param options
     * @return
     */

    @POST("web/growRecordImgUpload")
    Observable<BASE> growRecordImgUpload(@QueryMap Map<String, Object> options);

    /**
     *  手机注册
     * @param options
     * @return
     */

    @POST("web/mobileRegister")
    Observable<USER> mobileRegister(@QueryMap Map<String, String> options);

    /**
     *  更新用户信息
     * @param options
     * @return
     */

    @POST("web/updateMyBaseInfo")
    Observable<USER> updateMyBaseInfo(@QueryMap Map<String, String> options);

    /**
     *  修改密码
     * @param options
     * @return
     */

    @POST("web/updatePwd")
    Observable<USER> updatePwd(@QueryMap Map<String, String> options);
    /**
     * 获取我的基本信息
     * @param options
     * @return
     */

    @POST("web/getMyBaseInfo")
    Observable<USER> getMyBaseInfo(@QueryMap Map<String, String> options);



    /**
     * 设置默认冲泡的奶粉
     */
    @POST("web/setUserMilk")
    Observable<String> setUserMilk(@QueryMap Map<String, String> options);
    /**
     * 设置默认冲泡的奶粉
     */
    @POST("web/setUserVaccineTime")
    Observable<String> setUserVaccineTime(@QueryMap Map<String, String> options);
    /**
     * 更改疫苗提醒开关状态
     */
    @POST("web/setUserVaccineRemindStatus")
    Observable<String> setUserVaccineRemindStatus(@QueryMap Map<String, String> options);
    /**
     * 更新头像
     * @param options
     * @return
     */

    @POST("web/getNextUserVaccineInfo")
    Observable<BASE> getNextUserVaccineInfo(@QueryMap Map<String, String> options);

    /**
     * 奶粉品牌列表
     */
    @POST("web/listMilkInfoCategory")
    Observable<List<MilkInfo>> getListMilkCategory(@QueryMap Map<String, String> options);

    /**
     * 获取验证码
     */
    @POST("Flow/v2/VerifyMobileNumber/{name}")
    Observable<String> getCode(@Path("name") String name);
    /**
     * 更改疫苗状态
     */
    @POST("web/updateUserVaccineStatus")
    Observable<String> updateUserVaccineStatus(@QueryMap Map<String, String> options);

}
