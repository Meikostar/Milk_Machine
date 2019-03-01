package socket.laserprotocol.entry;



import com.canplay.medical.R;
import com.canplay.milk.util.FR;

import java.io.Serializable;

import socket.util.HexUtil;

/**
 * Created by linquandong on 17/1/6.
 * 主控制状态
 */
public class ControlState implements Serializable {
    public static final int POWER = 0;//控制电源接通
    public static final int SYSTEM = 1;//系统就绪
    public static final int LIGHT = 2;//出光
    public static final int TEMP_LEAK = 3;//超温
    public static final int FAULT = 4;//故障
    public static final int ENABLE = 5;//使能开
    public static final int LOCAL = 6;//定位光关开
    public static final int INNERCONTROL = 7;//内控模式
    public static final int JERK = 8;//急停
    public static final int CLODWATER_LOCK = 9;//冷水互锁
    public static final int END = 10;//程序结束
    public static final int WET_LEAK = 11;//超湿
    public static final int QCW = 12;//QCW模式
    public static final int CONTROL_RIGHT = 29;//网络控制
    public static final int PORT_RIGHT = 30;//串口控制
    public static ControlState instance;
    private String controlState;

    private ControlState(){

    }

    public static ControlState shareInstance(){
        if(instance== null){
            instance = new ControlState();
        }
        return instance;
    }

    public String getState(String controlDate, int state){
        String bit = HexUtil.mutilHexStringToBitStr(controlDate);
//        int len = bit.length();
//        int index = len - state - 1;
        String indexdate = bit.substring(state,state+1);
        switch (state){
            case POWER:
                return indexdate.equals("1")? FR.str(R.string.poweron):FR.str(R.string.poweroff);
            case SYSTEM:
                return indexdate.equals("1")? FR.str(R.string.systemon):FR.str(R.string.systemoff);
            case LIGHT:
                return indexdate.equals("1")? FR.str(R.string.lighton):FR.str(R.string.lightoff);//激光指示灯
            case TEMP_LEAK:
                return indexdate.equals("1")? FR.str(R.string.temp_leak):FR.str(R.string.temp_nor);
            case FAULT:
                return indexdate.equals("1")? FR.str(R.string.faulton):FR.str(R.string.faultoff);
            case ENABLE:
                return indexdate.equals("1")? FR.str(R.string.laseron):FR.str(R.string.laseroff);//激光开关
            case LOCAL:
                return indexdate.equals("1")? FR.str(R.string.localon):FR.str(R.string.localoff); //(红光，定位光)
            case INNERCONTROL:
                return indexdate.equals("1")? FR.str(R.string.inner):FR.str(R.string.wide);
            case JERK:
                return indexdate.equals("1")? FR.str(R.string.jerkon):FR.str(R.string.jerkoff);
            case CLODWATER_LOCK:
                return indexdate.equals("1")? FR.str(R.string.coldlock):FR.str(R.string.coldnor);
            case END:
                return indexdate.equals("1")? FR.str(R.string.pro_start):FR.str(R.string.pro_end);
            case WET_LEAK:
                return indexdate.equals("1")? FR.str(R.string.wet_leak):FR.str(R.string.wet_nor);
            case QCW:
                return indexdate.equals("1")? FR.str(R.string.qcw):FR.str(R.string.qcw_off);
            case CONTROL_RIGHT:
                return indexdate.equals("1")? FR.str(R.string.contrl):FR.str(R.string.contrl_off);
            case PORT_RIGHT:
                return indexdate.equals("1")? FR.str(R.string.apply):FR.str(R.string.apply_cancel);
        }
        return "";
    }

    public String getInnerId(String controlDate, int state){
        String bit = HexUtil.mutilHexStringToBitStr(controlDate);
//        int len = bit.length();
//        int index = len - state - 1;
        String indexdate = bit.substring(state,state+1);
        return String.format("%s%02d%s","S",state+1,indexdate);
    }

    public boolean sate(int state){
        try {
            String bit = HexUtil.mutilHexStringToBitStr(controlState);
            String indexdate = bit.substring(state,state+1);
            return Integer.parseInt(indexdate)>0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean sate(String controlStr, int state){
        setControlSate(controlStr);
        return sate(state);
    }

    public void setControlSate(String controlState) {
        this.controlState = controlState;
    }
}
