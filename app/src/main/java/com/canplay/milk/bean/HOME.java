package com.canplay.milk.bean;

/**
 * Created by mykar on 17/4/26.
 */
public class HOME {
//"nextTime":1531152000000,	//下一次疫苗时间，为null时没有
//        "milkInfo":{	//奶粉信息
//        "id":1,
//                "type":"system",
//                "userId":0,
//                "name":"爱他美",	//品牌
//                "subName":"爱他美-金装",	//名称
//                "pinyin":"aitamei",
//                "barCode":"121",
//                "grade":2,	//段数，123段
//                "waterTemperature":90,
//                "milkWeight":10,
//                "waterQuantity":100,
//                "createTime":1527940445000,
//                "validStatus":1,
//                "gradeDescription":"6~12个月"		//段数描述
//    },
//            "todayMilk":90,	//今日奶量
//            "article":{
//        "id":2,
//                "title":"test",	//文章标题
//                "shortContent":"123",
//                "resoureKey":"http://oss3m8u82.bkt.clouddn.com/FvURr2vBn0mpESeMTDbYAmNkHtF6",
//                "createTime":1525878914000,	//创建时间
//                "updateTime":1525878914000,
//                "validStatus":1
//    }
    public long nextTime;
    public long createTime;
    public long updateTime;
    public long times;
    public String type;
    public int validStatus;

    public HOME milkInfo;
    public HOME article;
    public String id;
    public String userId;
    public String name;
    public String subName;
    public String pinyin;
    public String barCode;
    public String grade;
    public String waterTemperature;
    public String milkWeight;
    public String waterQuantity;
    public String gradeDescription;
    public String todayMilk;
    public String title;
    public String shortContent;
    public String resoureKey;



}
