package com.canplay.milk.bean;

/**
 * Created by mykar on 17/4/26.
 */
public class BASE {
//"previousTime":1528560000000,	//上一次疫苗时间，没有为null
//        "nextTime":1531152000000,	//下一下疫苗时间，没有为null
//        "nextVaccine":{	//下一次疫苗信息
//        "id":8,
//                "name":"百白破疫苗",	//疫苗名称
//                "type":3,
//                "isNecessary":1,	//是否必打（免费）
//                "frequency":0,	//第几次，0为没有第几次
//                "orderBy":0,
//                "validStatus":1
//    }
    public long createTime;
    public long times;
    public long type;
    public int validStatus;
    public int isNecessary;
    public long nextTime;
    public long previousTime;
    public String frequency;
    public BASE nextVaccine;
    public String sdkUrlName;
    public String userVaccineId;
    public String id;
    public String name;
    public String orderBy;
    public String code;
    public String updateInfo;
    public String data;
    public String msg;
    public String platformType;
    public String version;
    public String sumMilk;

    public long dateStr;

}
