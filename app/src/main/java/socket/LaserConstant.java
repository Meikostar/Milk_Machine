package socket;

/**
 * Created by linquandong on 16/12/9.
 */
public class LaserConstant {

    public static final String ERROR = "-1";
    public static final String LOGIN = "01";//登陆
    public static final String CONTROL = "02";//申请控制
    public static final String NETCONTROL = "7A";//互联网申请控制权发送


    public static final String OPERATE = "03";//其他操作命令

    public static final String ALLARG_S ="65";//101全通道查询
    public static final String REDLIGHT = "6B";//107红光
    public static final String REMOVEFAULT = "6C";//108解除故障
    public static final String LASERLIGHT = "7B";//123激光
    public static final String INNERSET = "69";//105 内控设定值

    public static final String WORKMODE = "6D";//109//内外控模式切换
    public static final String INNSERMODE= "6E";//110//内控时的模式选择
    public static final String PWM = "6F";//111 pwm查询回调
    public static final String PWMRETURN = "EF";//111 pwm提交回调
    public static final String RAMP = "70";//112 RAM
    public static final String RAMPRETURN = "F0";// RAM提交回调
  //奶粉机指令
    public static final String XTBAO = "B1";// 0xB1		心跳包
    public static final String CLEAN = "B2";// 0xB2		自清洗命令发送
    public static final String BUBBLE = "B2";// 0xB3		泡奶命令发送
    public static final String OUTWATER = "B4";// 0xB4		一键出水
    public static final String DISS = "B5";// 0xB4		一键出水
    public static final String STOP = "B6";// 0xB6		停止命令
    public static final String BIND = "B8";// 0xB8		绑定发送
    public static final String REPLY = "B9";// 0xB9		泡奶量回复




//    外网
    public static  final String SUBSCRIBE = "05";//订阅
    public static  final String RAILWAY = "06";//专线
    public static  final String DEVICE = "07";//设备

    //关于字符串位置
    public static final int DEVNAME_INDEX = 6*2; //设备名称
    public static final int CONTOR_INDEX = DEVNAME_INDEX+8*2; //控制符
    public static final int LEN_INDEX = CONTOR_INDEX+2; //业务数据长度
    public static final int BUS_INDEX = LEN_INDEX+2; //业务数据
    public static final int STATE_INDEX = CONTOR_INDEX +4;


}
