package socket.laserprotocol.entry;

import com.canplay.medical.R;
import com.canplay.milk.util.FR;

import java.io.Serializable;

import socket.util.HexUtil;

/**
 * Created by linquandong on 17/1/6.】
 * 故障码
 */
public class FaultCode implements Serializable {
    public static final int DRIVER1_FAULT = 0;//1号驱动板通讯故障
    public static final int DRIVER2_FAULT = 1;//2号驱动板通讯故障

    public static final int DRIVER3_FAULT = 2;//3号驱动板通讯故障
    public static final int DRIVER4_FAULT = 3;//4号驱动板通讯故障
    public static final int AD_FAULT = 4;//AD板通讯故障
    public static final int DIODE_TEMP_LEAK = 5;//二极体模块超温
    public static final int DRIVER_TEMP_LEAK = 6;//驱动模块超温
    public static final int WATER_TEMP_LEAK = 7;//水温超温
    public static final int OPTICAL_TEMP_LEAK = 8;//光纤温度超温
    public static final int LASER_REFLET_UP = 9;//激光反射能量超上限
    public static final int LASER_OUT_DOWN = 10;//激光输出能量超下限
    public static final int DIODE_SHORCUT = 11;//二极体短路故障
    public static final int OPTICAL_OFF = 12;//光纤断开
    public static final int WET_LEAK = 13;//超湿
    public static final int COLDWATER_LOCK = 14;//冷水互锁
    public static final int JERK = 15;//急停
    public static final int LOCAL_LIGHT = 16;//定位光
    private static final int SPIKE_PLUSE = 17;//窄脉冲

    //    public static final int REDLINGHT_FAULT = 16;//红光故障
    public static FaultCode instance;

    private FaultCode(){}
    public static FaultCode shareInstance(){
        if(instance== null){
            instance = new FaultCode();
        }
        return instance;
    }

    public String getState(String controlDate, int state){
        String bit = HexUtil.mutilHexStringToBitStr(controlDate);
        String indexdate = bit.substring(state,state+1);
        switch (state){
            case DRIVER1_FAULT:
                return indexdate.equals("1")? FR.str(R.string.driver1):FR.str(R.string.driver1_rm);
            case DRIVER2_FAULT:
                return indexdate.equals("1")? FR.str(R.string.driver2):FR.str(R.string.driver2_rm);
            case DRIVER3_FAULT:
                return indexdate.equals("1")? FR.str(R.string.driver3):FR.str(R.string.driver3_rm);
            case DRIVER4_FAULT:
                return indexdate.equals("1")? FR.str(R.string.driver4):FR.str(R.string.driver4_rm);
            case AD_FAULT:
                return indexdate.equals("1")? FR.str(R.string.ad):FR.str(R.string.ad_rm);
            case DIODE_TEMP_LEAK:
                return indexdate.equals("1")? FR.str(R.string.diode_temp_leak):FR.str(R.string.diode_temp_nor);
            case DRIVER_TEMP_LEAK:
                return indexdate.equals("1")? FR.str(R.string.driver_temp):FR.str(R.string.driver_temp_rm);
            case WATER_TEMP_LEAK:
                return indexdate.equals("1")? FR.str(R.string.water_temp):FR.str(R.string.water_temp_rm);
            case OPTICAL_TEMP_LEAK:
                return indexdate.equals("1")? FR.str(R.string.optical_temp):FR.str(R.string.optical_temp_rm);
            case LASER_REFLET_UP:
                return indexdate.equals("1")? FR.str(R.string.reflet_up):FR.str(R.string.reflet_nor);
            case LASER_OUT_DOWN:
                return indexdate.equals("1")? FR.str(R.string.out_down):FR.str(R.string.out_nor);
            case DIODE_SHORCUT:
                return indexdate.equals("1")? FR.str(R.string.diode_short):FR.str(R.string.diode_short_rm);
            case OPTICAL_OFF:
                return indexdate.equals("1")? FR.str(R.string.optical_cut):FR.str(R.string.optical_cut_rm);
            case WET_LEAK:
                return indexdate.equals("1")? FR.str(R.string.wet_up):FR.str(R.string.wet_up_rm);
            case COLDWATER_LOCK:
                return indexdate.equals("1")? FR.str(R.string.coldwaterlock):FR.str(R.string.coldwaterlock_rm);
            case JERK:
                return indexdate.equals("1")? FR.str(R.string.jerk):FR.str(R.string.jerk_rm);
            case LOCAL_LIGHT:
                return indexdate.equals("1")? FR.str(R.string.locallight):FR.str(R.string.locallight_rm);
            case SPIKE_PLUSE:
                return indexdate.equals("1")? FR.str(R.string.spike_pluse):FR.str(R.string.spike_pluse_rm);

        }
        return "";
    }
    public String getInnerId(String controlDate, int state){
        String bit = HexUtil.mutilHexStringToBitStr(controlDate);
        String indexdate = bit.substring(state,state+1);
        if(indexdate.equals("1")){
            return String.format("%s%02d%s","E",state+1,indexdate);
        }else {
            return String.format("%s%02d%s","X",state+1,indexdate);
        }
    }

    public boolean sate(String controlDate, int state){
        try {

            String bit = HexUtil.mutilHexStringToBitStr(controlDate);
            String indexdate = bit.substring(state,state+1);
            return Integer.parseInt(indexdate)>0;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public int getStateValue(String controlDate, int state){
        String bit = HexUtil.mutilHexStringToBitStr(controlDate);
        String indexdate = bit.substring(state,1);
        return Integer.parseInt(indexdate);

    }
}
