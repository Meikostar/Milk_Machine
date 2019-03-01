package socket.laserprotocol;



import com.canplay.medical.R;
import com.canplay.milk.bean.SETTING;
import com.canplay.milk.util.FR;
import com.canplay.milk.util.LaserSetting;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;

import socket.LaserConstant;
import socket.laserprotocol.entry.Grallry;

/**
 * Created by linquandong on 16/12/5.
 * 全通道查询
 */
public class AllargProtocol extends Baseprotocol {
    public ArrayList<Grallry> grallries = new ArrayList<>();

    public AllargProtocol() {
        controlStr = LaserConstant.ALLARG_S;
    }

    public static final int ALLGALLRYLEN = 12 * 18;
    public static final int OTHREPARAMLEN = 52;
    public static final int STATECODELEN = 8;

    public int akw;//设定电流
    public int watertemper;//水温
    public int opticaltemper1;//光纤温度
    public int opticaltemper2;//光纤温度
    public int opticaltemper3;//光纤温度
    public int humidity;//湿度
    public int envtemper;//环境温度
    public int laserpowerset;//激光功率设定值
    public int laserpowerfk;//激光反馈值
    public int settype;//设定方式
    public int projectcode;//程式号
    public long time;//实时时间
    public int maxpower;//最大功率
    public int vapourpress;//汽压
    public int waterspeed;//水速
    public int drivernum;//驱动板数量
    public int grallrynum;//每块驱动板通道数
    public int backup;//备用
    public float pdsum;//总功率
    public String controlState;
    public String faultState;

    /**
     * 获取运行时的显示值
     *
     * @param i
     * @return
     */
    public String getRungingValue(int i) {
        String value = "";
        switch (i) {
            case 0:
                value = watertemper + FR.str(R.string.centigrade);
                break;
            case 1:
                value = envtemper + FR.str(R.string.centigrade);
                break;
            case 2:
                value = humidity + "%";
                break;
            case 3:
                value = opticaltemper1 + FR.str(R.string.centigrade);
                break;
            case 4:
                value = opticaltemper2 + FR.str(R.string.centigrade);
                break;
            case 5:
                value = opticaltemper3 + FR.str(R.string.centigrade);
                break;
            case 6:
                value = vapourpress / 10.0f + "Kpa";
                break;
            case 7:
                value = waterspeed + "L/min";
                break;

        }
        return value;

    }

    //光电效率=光功率/耗电功率（激光功率反馈值）
    public float getlightpd() {
        float totalpower = getTotalPower();
        if (totalpower == 0) {
            return 0;
        }
        return laserpowerfk/totalpower;
    }

    @Override
    public void encode(BaseLaserDate laserDate) {
        super.encode(laserDate);
        try {
            akw = Integer.parseInt(busStr.substring(0, 2), 16);

            String allgallry = busStr.substring(2, 2 + ALLGALLRYLEN);
            String otherParm = busStr.substring(2 + ALLGALLRYLEN, 2 + OTHREPARAMLEN + ALLGALLRYLEN);

            controlState = busStr.substring(2 + OTHREPARAMLEN + ALLGALLRYLEN, 10 + OTHREPARAMLEN + ALLGALLRYLEN);
            faultState = busStr.substring(10 + OTHREPARAMLEN + ALLGALLRYLEN, 18 + OTHREPARAMLEN + ALLGALLRYLEN);

            encodeAllGrallry(allgallry, akw);
            encodeParam(otherParm);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void encodeParam(String otherParm) {
        watertemper = Integer.parseInt(otherParm.substring(0, 2), 16);

        opticaltemper1 = Integer.parseInt(otherParm.substring(2, 4), 16);

        opticaltemper2 = Integer.parseInt(otherParm.substring(4, 6), 16);

        opticaltemper3 = Integer.parseInt(otherParm.substring(6, 8), 16);

        humidity = Integer.parseInt(otherParm.substring(8, 10), 16);

        envtemper = Integer.parseInt(otherParm.substring(10, 12), 16);

        laserpowerset = Integer.parseInt(otherParm.substring(12, 16), 16);

        laserpowerfk = Integer.parseInt(otherParm.substring(16, 20), 16);

        settype = Integer.parseInt(otherParm.substring(20, 22), 16);

        projectcode = Integer.parseInt(otherParm.substring(22, 24), 16);

        time = Long.parseLong(otherParm.substring(24, 32), 16);

        maxpower = Integer.parseInt(otherParm.substring(32, 36), 16);

        vapourpress = Integer.parseInt(otherParm.substring(36, 40), 16);

        waterspeed = Integer.parseInt(otherParm.substring(40, 44), 16);

        drivernum = Integer.parseInt(otherParm.substring(44, 46), 16);

        grallrynum = Integer.parseInt(otherParm.substring(46, 48), 16);

        backup = Integer.parseInt(otherParm.substring(48, 52), 16);
    }

    private void encodeAllGrallry(String allgallry, int akw) {
        int index = 0;
        pdsum = 0;
        for (int i = 0; i < allgallry.length(); i += 12) {
            String grallyStr = allgallry.substring(i, i + 12);
            try {
                grallries.get(index).decode(grallyStr, index);
            } catch (Exception e) {
                Grallry grallry = new Grallry(grallyStr, index);
                grallry.akw = akw;
                grallries.add(grallry);
            }
            pdsum += grallries.get(index).pd;
            index++;
        }
    }

    @Override
    public String makeCommunicateStr() {
        String hefix = controlStr;   //控制命令
        String data = "00";//没有数据
        return makCommnuByHeaderData(hefix, data);
    }


    public String getTime() {
        return time + "";
    }

    public float getParam(int postion) {
        try {
            SETTING setting = LaserSetting.shareInstance().getSetting(postion);
            Field field = getClass().getField(setting.fieldName);
            int value = (Integer) field.get(this);
            float result = 0.0f;
            if(setting.fieldName.equals("laserpowerset")){//计算百分比
                if(maxpower != 0){
                    result = laserpowerset*100/maxpower;
                }
            }else if(setting.fieldName.equals("laserpowerfk")){//功率反馈值/设定功率值
                if(maxpower!=0){
                    result = laserpowerfk*100/maxpower;
                }
            }else {
                result = value * setting.scale;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
//
//        switch (position){
//            case 0:
//                return humidity;
//            case 1:
//                return envtemper;
//            case 2:
//                return vapourpress;
//            case 3:
//                return waterspeed;
//        }
        return 0;
    }

    /**
     * 获取电源的电压
     *
     * @param positon
     * @return 15、16、17、18通道反馈电压分别代表1#、2#、3#、4#板的电源电压。
     */
    public String getGrallDy(int positon) {
        int vfk = getGrallDyValue(positon);
        return new DecimalFormat("0.0").format(vfk/10.f)+"V";
    }

    /**
     * 总功率
     * @return
     */
    public float getTotalPower() {
        float value = 0;
        for (int i = 0;i<drivernum;i++){
            value+=getGrakkDlValue(i)*getGrallDyValue(14+i);
        }
        return value;
    }

    /**
     * 获取电源电流
     *
     * @param positon
     * @return 驱动板通道板的电流相加
     */
    public String getGrallDL(int positon) {
        int value = getGrakkDlValue(positon);
        return new DecimalFormat("0.0").format(value / 10.f) + "A";
    }

    public int getGrakkDlValue(int positon) {
        try {
            int start = positon * grallrynum;
            int end = (positon + 1) * grallrynum;
            int dlSum = 0;
            for (int i = start; i < end; i++) {
                dlSum += grallries.get(i).lfk;
            }
            return dlSum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getGrallDyValue(int positon) {
        try {
            return grallries.get(positon).vfk;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
