package com.canplay.milk.bean;

import java.util.List;

/**
 * Created by mykar on 17/4/26.
 */
public class Vaccines {
//    "userVaccineList":[
//
//    {
//        "id":12,    //用户疫苗记录id
//            "userId":1,
//            "vaccineId":1,    //疫苗的id
//            "status":0,
//            "createTime":1527995949000,
//            "updateTime":1527995949000,
//            "name":"卡介苗",    //疫苗名称
//            "type":0,
//            "isNecessary":1,    //是否必打（免费）
//            "frequency":0,    //第几次，0为没有第几次
//            "orderBy":0
//        "typeName":"出生",    //类型名称
//            "currentDate":"2018-04-03"    //当前时间
        public List<Vaccines> userVaccineList;
        public long createTime;
        public long updateTime;
        public int frequency;
        public int isNecessary;
        public String typeName;
        public String id;
        public String orderBy;
        public String userId;
        public String vaccineId;
        public String status;
        public String name;
        public String type;
        public int types ;
        public String currentDate;

    }
