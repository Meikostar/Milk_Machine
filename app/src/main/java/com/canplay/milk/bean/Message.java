package com.canplay.milk.bean;

import java.io.Serializable;


public class Message implements Serializable{

//        "messageId":"3113690d-7c45-e711-b9e4-2c44fd9333fc",
//            "title":"测试文本消息",
//            "content":"测试文本消息",
//            "type":"Text",
//            "from":"System",
//            "status":"Active",
//            "createdDateTime":"1496073600000"


    public Long createdDateTime;
    public String messageId;
    public String title;
    public String content;
    public String type;
    public String status;
    public String from;

}
