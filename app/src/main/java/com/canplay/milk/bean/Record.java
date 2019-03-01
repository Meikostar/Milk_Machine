package com.canplay.milk.bean;

import java.util.List;

/***
 * 功能描述:
 * 作者:chenwei
 * 时间:2016/12/26
 * 版本:
 ***/
public class Record {

//    {
//        "date":"1489622400000",
//            "items":[
//        {
//            "title":"添加药物",
//                "category":"Medicine",
//                "content":"添加药物Medicine4",
//                "createdDateTime":"1489702598763"
//        },
//    {  
//        "payloadId":"bfe757f6-300c-e711-b9e4-2c44fd9333fc",
//        "userId":"ccda9cf7-d871-41a5-8548-77022c54f1bf",
//        "deviceType":"Blood pressure monitor",
//        "deviceTypeName":"血压",
//        "value":"{\"version\":\"1.5\",\"high\":\"160.2\",\"low\":\"110.1\",\"pulse\":\"0\",\"timeStamp\":1489622400}",
//        "createdDateTime":"1489879012627"



    public Boolean isCompleted;
    public String userId;
    public String deviceType;
    public String deviceTypeName;
    public String value;
    public String title;
    public String payloadId;
    public String category;
    public String content;
    public String version;
    public String high;
    public String bgl;
    public String low;
    public String pulse;
    public long timeStamp;
    public List<Record> items;

    public long createdDateTime;
    public long date;


}
