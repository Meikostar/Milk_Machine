package com.canplay.milk.base;

/***
 * 功能描述:RxBus消息订阅
 * 作者:chenwei
 * 时间:2016/12/15
 * 版本:1.0
 ***/

public class SubscriptionBean {
    public static int UPDATE=98;
    public static int GROUP=100;
    public static int RE_LOGIN=99;
    public static int NOFIFY=101;

    public static int MENU_REFASHS=102;

    public static int MENU_SCAN=103;//扫一扫
    public static int MESURE=104;//测量
    public static int FINISH=105;//
    public static int FINISHS=1099;//
    public static int REFEST_SET=106;//
    public static int REFESH_MLIK=107;//药物提醒列表刷新
    public static int SCAN=108;//药物提醒列表刷新
    public static int MILKINFO=109;//药物提醒列表刷新
    public static int REFESH_TIME=110;//药物提醒列表刷新
    public static int RESTART=111;//重新连接
    public static int BIND=112;//绑定状态通知
    public static int OFF=113;//断开连接
    public static int CONNECT=114;//已连接
    public static int WIFISTATE=115;//wifi状态
    public static int WAIT=116;//wifi状态
    public static int NEWINSTANCES=1117;//wifi状态
    public static int CLOSE=1118;//wifi状态
    public static int WATER=1119;//wifi状态
    public static int MILK=1120;//wifi状态
    public static int CLERS=1121;//wifi状态
    public static int MILKS=1122;//wifi状态



    /**
     * 重新登入
     */

    public static <T>RxBusSendBean createSendBean(int type,T t){
        RxBusSendBean<T> busSendBean = new RxBusSendBean();
        busSendBean.type = type;
        busSendBean.content = t;
        return busSendBean;
    }

    public static class RxBusSendBean<T>{
        public int type;
        public T content;
    }

}
