package com.canplay.milk.bean;


import java.util.List;

/**
 * Some command here.
 *
 * @author Vincent Cheung
 * @since  Jan. 22, 2015
 */
public class Remind {

//		"list":[{	//提醒列表
//		"id":1,	//提醒记录id
//				"userId":1,	//用户记录id
//				"name":"测试2",	//提醒名称
//				"remindTime":"08:02",	//提醒的时间点
//				"repeatDay":"1,2,3,4,5",	//闹钟提醒的天，周几
//				"validStatus":1,	//设置是否有效，1有效，0无效
//				"createTime":1528021664000	//记录创建的时间
//	}],
//			"userRemindStatus":1	//提醒总开关，1开，0关
	public List<Remind> list;
	public String id;
	public String userId;
	public String name;
	public String remindTime;
	public int validStatus;
	public long createTime;
	public int userRemindStatus;
	public String repeatDay;

}
