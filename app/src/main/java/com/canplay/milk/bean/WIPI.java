package com.canplay.milk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mykar on 17/4/26.
 */
public class WIPI implements Serializable{
//    "id": "535",
//	"pageNum":1,
//            "pageSize":3,
//            "startRow":0,
//            "endRow":3,
//            "total":3,	//总行数
//            "pages":1,
//            "list":[
//    {
//        "id":2,
//            "title":"test",	//标题
//            "shortContent":"123",	//简介
//            "resoureKey":"http://oss3m8u82.bkt.clouddn.com/FvURr2vBn0mpESeMTDbYAmNkHtF6",	//展示图片
//            "createTime":1525878914000,	//创建时间
//            "updateTime":1525878914000,
//            "validStatus":1
    public int pageSize;
    public int pageNum;
    public int endRow;
    public int startRow;
    public int total;
    public int pages;
        public List<WIPI> list;
    public long createTime;
    public long updateTime;
    public String id;
    public String title;
    public String shortContent;
    public String resoureKey;
    public String validStatus;
    public String version;
    public String userId;
    public String content;
    public String imgResourceKeys;



}
