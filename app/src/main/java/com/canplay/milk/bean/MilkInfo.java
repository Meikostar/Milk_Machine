package com.canplay.milk.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mykar on 17/4/26.
 */
public class MilkInfo implements Serializable {
//    "id": "535",
//            "name": "爱尔巴桑",
//            "parent_id": "534",
//            "type": "city"

    public int pageSize;
    public int pageNum;
    public int startRow;
    public int endRow;
    public int validStatus;
    public int total;
    public int pages;
    public List<MilkInfo> list;
    public String id;
    public String type;
    public String userId;
    public String subName;
    public String name;
    public String pinyin;
    public String barCode;
    public String grade;
    public String waterTemperature;
    public String milkWeight;
    public String waterQuantity;
    public long createTime;
    public String surcharge;
    public String gradeDescription;
    public String content;

}
