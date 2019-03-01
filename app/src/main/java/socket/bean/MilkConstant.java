package socket.bean;

import android.os.CountDownTimer;

import com.canplay.milk.base.BaseApplication;
import com.canplay.milk.base.RxBus;
import com.canplay.milk.base.SubscriptionBean;
import com.canplay.milk.util.TextUtil;

import socket.util.CRC16Modbus;
import socket.util.HexUtil;
import udpsocket.core.TcpClientManager;

/**
 * Created by linquandong on 16/12/9.
 */
public class MilkConstant {


    public static String HEAD = "";//头部
    public static String HD = "";//头部
    public static String EQUIPT = "";//头部
    public static String HEADS = "AABB";//头部
    private static CRC16Modbus crc16Modbus = new CRC16Modbus();
    public static String TAIL = "BBAA";//101全通道查询
    public static String COMMEND = "B1";//指令
    public static String COMMENDS= "B1";//指令
    public static String CONTENT = "";//数据包内容
    public static String LENGTH = "09";//数据包内容
    public static int RECE = 0;//数据包内容

    public static String sendCommend() {

        if(TextUtil.isNotEmpty(HEAD)){
            return HEADS + getCRC() + crc16Modbus.getCrcHexStrReverse(HexUtil.hexStringToByte(getCRC())) + TAIL;
        }else {
           return "";
        }
    }
    public static String sendCommends() {

        if(TextUtil.isNotEmpty(HEAD)&& TcpClientManager.getInstance().isConnected()){
            return HEADS + getCRCS() + crc16Modbus.getCrcHexStrReverse(HexUtil.hexStringToByte(getCRCS())) + TAIL;
        }else {
            HEAD="";
            return "";
        }
    }
    public static String getCRC() {
        return LENGTH + HEAD.substring(6, HEAD.length()) + CONTENT + COMMEND;
    }
    public static String getCRCS() {
        return LENGTH + HEAD.substring(6, HEAD.length()) + CONTENT + COMMENDS;
    }
    public static String getCommend(String content) {

        return content.substring(20, 22);
    }

    public static String getContent(String content) {
        return content.substring(22, content.length() - 6);
    }

    public static void selectCommnt(int poition, String cont) {
        if (poition == 1) {
            LENGTH = "09";
            if(TextUtil.isNotEmpty(cont)){
                COMMENDS = "B1" + cont;//心跳包

            }else {
                COMMENDS = "B1" + "01";//心跳包
                BaseApplication.send=2;
            }
        } else if (poition == 2) {
            CONT="";
            RECE=1;
            LENGTH = "0A";
            COMMEND = "B2" + cont;//自清洗命令发送
            hert_time = 1000;

            BaseApplication.send=1;
            SEND = 1;
        } else if (poition == 3) {
            CONT="";
            RECE=2;
            LENGTH = "0D";
            COMMEND = "B3" + cont;//泡奶命令发送
            hert_time = 1000;

            BaseApplication.send=1;
            SEND = 1;
        } else if (poition == 4) {
            CONT="";
            RECE=3;
            LENGTH = "0A";
            COMMEND = "B4" + cont;//一键出水
            hert_time = 1000;

            BaseApplication.send=1;
            SEND = 1;
        } else if (poition == 5) {
            CONT="";
            RECE=4;
            LENGTH = "13";

            COMMEND = "B5" + cont;//解绑发送
            BaseApplication.send=1;
        } else if (poition == 6) {
            CONT="";
            LENGTH = "09";
            COMMEND = "B6" + cont;//停止命令
            BaseApplication.send=1;
        } else if (poition == 7) {
            CONT="";
            LENGTH = "09";
            COMMEND = "B7" + cont;//心跳包
            BaseApplication.send=1;
        } else if (poition == 8) {
            CONT="";
            LENGTH = "13";
            COMMEND = "B8" + cont;//绑定发送
        } else if (poition == 9) {
            CONT="";
            LENGTH = "09";
            COMMEND = "B9" + cont;//泡奶量回复
            hert_time = 1000;
            BaseApplication.send=1;
            SEND = 1;
        }
    }
    private static CountDownTimer countDownTimer3;
   public static void startW() {
       try {
           Thread.sleep(2000);
           if(RECE==1){
               RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.WAIT,"自清洗指令发送失败"));
               RECE=0;
           }else if(RECE==2) {
               RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.WAIT,"泡奶指令发送失败"));
               RECE=0;
           }else if(RECE==2) {
               RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.WAIT,"出水指令发送失败"));
               RECE=0;
           }else if(RECE==2) {
               RxBus.getInstance().send(SubscriptionBean.createSendBean(SubscriptionBean.WAIT,"解绑指令发送失败"));
               RECE=0;
           }

       } catch (InterruptedException e) {
           e.printStackTrace();
       }


   }
    public static String MilkState(String commend) {
        String content = "";
        String cont = commend.substring(20, 22);
        String contents = commend.substring(22, commend.length() - 6);
        if (cont.equals("A1")) {
            String info = getInfo(contents);
            content = "A1#"+info;
        } else if (cont.equals("A2")) {//回复自清洗
            content = "A2#" + clearMilk(contents);
            RECE=0;
        } else if (cont.equals("A3")) {//回复泡奶命令
            RECE=0;
            content = "A3#" + pushMilk(contents);
        } else if (cont.equals("A4")) {//回复一键出水
            RECE=0;
            content = "A4#" + pushWahter(contents);
        } else if (cont.equals("A5")) {//解除绑定回复
            RECE=0;
            content = "A5#" + unBind(contents);
        } else if (cont.equals("A6")) {//	停止命令回复
            content = "A6#" + stopMilk(contents);
        } else if (cont.equals("A7")) {//UDP广播
        } else if (cont.equals("A8")) {//绑定回复
            content = "A8#" + binds(contents);

        } else if (cont.equals("A9")) {//本次泡奶量
            content = "A9#" + contents;
        }
        return content;
    }

    public static String SB="";//设备状态
    public static String JS="";//接收状态
    public static String HH="";//混合状态
    public static String SX="";//水箱状态
    public static String NF="";//奶粉罐状态
    public static String NP="";//奶瓶状态
    public static String SW="01";//水温
    public static String STATE;//状态
    public static String CONT;//状态
    public static int SEND;//状态
    public static long hert_time = 6000;//奶瓶状态

    public static String getInfo(String cont) {
        String content="";

        SB = cont.substring(0, 2);

        JS = cont.substring(2, 4);
        HH = cont.substring(4, 6);
        SX = cont.substring(6, 8);
        NF = cont.substring(8, 10);
        NP = cont.substring(10, 12);
        if(cont.length()>12){
                    SW = cont.substring(12, 14);

        }
        if (SB.equals("01")) {
            if (SEND == 1) {
                SEND = 2;

            } else {
                SEND = 0;
                hert_time = 6000;

            }

        } else {
            SEND=0;
        }
        if (!HH.equals("01")) {
           content="混合器故障";
        }if (!SX.equals("01")) {
            content="低水压警告";
        }if (!NF.equals("01")) {
            content="奶粉罐故障";
        }if (!NP.equals("01")) {
            content="奶瓶故障";
        }if (!SW.equals("01")) {
            content="入水温度过高";
        }
        CONT=content;
        return content;
    }

    /**
     * 自清洗命令
     *
     * @param content
     * @return
     */
    public static String clearMilk(String content) {
        String cont = "";
        if (content.length() == 6) {
            if (content.substring(4, 6).equals("01")||content.substring(4, 6).equals("03")) {
                if (content.substring(0, 2).equals("01")) {//机器一切正常，可接收任何数据
                    cont = "设备正在清洗中...";
                } else if (content.substring(0, 2).equals("02")) {//设备需要进行自清洗
                    cont = "设备需要进行自清洗";
                } else if (content.substring(0, 2).equals("03")) {//机器正在自清洗
                    cont = "机器正在自清洗";
                } else if (content.substring(0, 2).equals("04")) {//机器正在泡奶中
                    cont = "机器正在泡奶中无法清洗";
                } else if (content.substring(0, 2).equals("05")) {//机器正在一键冲水中
                    cont = "机器正在一键冲水中无法清洗";
                } else {
                    cont = "数据内容错误：" + content;
                }

            } else {
                if (content.substring(4, 6).equals("02")) {//混合器故障
                    cont = "混合器故障";
                } else if (content.substring(4, 6).equals("04")) {//低水压告警
                    cont = "低水压警告";
                } else if (content.substring(4, 6).equals("05")) {//奶瓶位置告警
                    cont = "奶瓶位置警告";

                } else {
                    cont = "数据内容错误：" + content;
                }
            }

            return cont;
        } else {
            return cont;
        }
    }

    /**
     * 泡奶命令
     *
     * @param content
     * @return
     */
    public static String pushMilk(String content) {
        String cont = "";
        if (content.length() == 6) {
            if (content.substring(4, 6).equals("01")) {
                if (content.substring(0, 2).equals("01")) {//机器一切正常，可接收任何数据
                    cont = "开始泡奶";
                } else if (content.substring(0, 2).equals("02")) {//设备需要进行自清洗
                    cont = "设备需要进行自清洗无法泡奶";
                } else if (content.substring(0, 2).equals("03")) {//机器正在自清洗
                    cont = "机器正在自清洗无法泡奶";
                } else if (content.substring(0, 2).equals("04")) {//机器正在泡奶中
                    cont = "机器正在泡奶中";
                } else if (content.substring(0, 2).equals("05")) {//机器正在一键冲水中
                    cont = "机器正在一键冲水中无法泡奶";
                } else {
                    cont = "数据内容错误：" + content;
                }

            } else {
                if (content.substring(4, 6).equals("02")) {//混合器故障
                    cont = "混合器故障";
                } else if (content.substring(4, 6).equals("03")) {//奶粉罐故障
                    cont = "奶粉罐故障";
                } else if (content.substring(4, 6).equals("04")) {//低水压告警
                    cont = "低水压告警";
                } else if (content.substring(4, 6).equals("05")) {//奶瓶位置告警
                    cont = "奶瓶位置告警";

                } else {
                    cont = "数据内容错误：" + content;
                }
            }

            return cont;
        }
        return cont;
    }

    /**
     * 一键冲水命令
     *
     * @param content
     * @return
     */
    public static String pushWahter(String content) {
        String cont = "";
        if (content.length() == 6) {
            if (content.substring(4, 6).equals("01")||content.substring(4, 6).equals("03")) {
                if (content.substring(0, 2).equals("01")) {//机器一切正常，可接收任何数据
                    cont = "开始冲水";
                } else if (content.substring(0, 2).equals("02")) {//设备需要进行自清洗
                    cont = "设备需要进行自清洗无法冲水";
                } else if (content.substring(0, 2).equals("03")) {//机器正在自清洗
                    cont = "机器正在自清洗无法冲水";
                } else if (content.substring(0, 2).equals("04")) {//机器正在泡奶中
                    cont = "机器正在泡奶中无法冲水";
                } else if (content.substring(0, 2).equals("05")) {//机器正在一键冲水中
                    cont = "机器正在一键冲水中";
                } else {
                    cont = "数据内容错误：" + content;
                }

            } else {
                if (content.substring(4, 6).equals("02")) {//混合器故障
                    cont = "混合器故障";
                }else if (content.substring(4, 6).equals("04")) {//低水压告警
                    cont = "低水压告警";
                } else if (content.substring(4, 6).equals("05")) {//奶瓶位置告警
                    cont = "奶瓶位置告警";

                } else {
                    cont = "数据内容错误：" + content;
                }
            }

            return cont;
        }
        return cont;
    }

    /**
     * 解除绑定设备命令
     *
     * @param content
     * @return
     */
    public static String unBind(String content) {
        String cont = "";
        if (content.length() == 22) {
            if (content.substring(20, 22).equals("01")) {
                cont = "解绑成功";

            } else {
                cont = "解绑失败";
            }

            return cont;
        }
        return cont;
    }

    /**
     * 绑定设备命令
     *
     * @param content
     * @return
     */
    public static String binds(String content) {
        String cont = "";
        if (content.length() == 24) {
            if (content.substring(22, 24).equals("01")) {
                cont = "绑定成功";

            } else {
                cont = "绑定失败";
            }

            return cont;
        }
        return cont;
    }

    /**
     * 停止命令
     *
     * @param content
     * @return
     */
    public static String stopMilk(String content) {
        String cont = "";
        if (content.length() == 2) {
            if (content.substring(0, 2).equals("01")) {

                cont = "操作成功";

            } else {
                cont = "操作失败";
            }

            return cont;
        }
        return cont;
    }

    /**
     * 本次泡奶量命令
     *
     * @param content
     * @return
     */
    public static String upMilk(String content) {
        String cont = "";
        if (content.length() != 0) {
            if (content.substring(content.length() - 2, content.length()).equals("01")) {

                cont = "操作成功";

            } else {
                cont = "操作失败";
            }

            return cont;
        }
        return cont;
    }

}
