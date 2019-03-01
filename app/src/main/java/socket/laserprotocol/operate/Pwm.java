package socket.laserprotocol.operate;


import com.canplay.medical.R;
import com.canplay.milk.util.FR;
import com.canplay.milk.util.StringUtils;
import com.canplay.milk.view.OscliiChart.OscliiPoint;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import socket.LaserConstant;
import socket.util.HexUtil;

/**
 * Created by linquandong on 17/2/6.
 */
public class Pwm extends Operate {

    public static final float UNINT_FRE = 1f;//频率单位
    public static final float UNINT_DC = 0.1f;
    public static final float UNINT_VALUE = 0.01f;

    public boolean isWrite;
    public String busId = LaserConstant.PWM;
    public int sercode; //序号
    public String name;//程序名
    public int frequency;//频率
    public float dc;//占空比
    public float value;//幅值
    public long time;
    public String mcStyle;//脉冲模式
    public String mcSource;//脉冲来源
    private String illegalDesc;

    public Pwm(boolean switchSate) {
        this.isWrite = switchSate;
    }

    public Pwm(String laserData) {
        encode(laserData);
    }

    @Override
    public String makeCommunicateStr() {
        String hefix = isWrite ? HexUtil.or(busId, "80") : HexUtil.or(busId, "00");   //开红光命令 写
        String serCode = String.format("%02x", sercode); //1byte
        String data = null;
        if (isWrite) {

            String nameStr = StringUtils.rightPad(HexUtil.cn2Hex(name),16,"20");//16进制20代表空格
            String frequencyStr = String.format("%04x", frequency);
            String dcStr = String.format("%04x", (int)(dc/UNINT_DC));

            String valueStr = String.format("%04x",(int)(value/UNINT_VALUE));
            String timeStr = String.format("%08x", time);
            String mcSourceStr = "00";
            data = serCode + nameStr + frequencyStr + dcStr +valueStr + timeStr+mcSourceStr+mcSourceStr;
        } else {
            data = serCode;
        }
        return makCommnuByHeaderData(hefix, data);
    }

    @Override
    public void encode(String laserData) {
        //TODO 解析laserData
        String serCodeStr = laserData.substring(0, 2);
        String nameStr = laserData.substring(2, 18);
        String frequencyStr = laserData.substring(18, 22);
        String dcStr = laserData.substring(22, 26);
        String valueStr = laserData.substring(26,30);
        String timeStr = laserData.substring(30, 38);
        mcStyle = laserData.substring(38, 40);
        mcSource = laserData.substring(40, 42);

        sercode = Integer.parseInt(serCodeStr, 16);
        frequency = Integer.parseInt(frequencyStr, 16);
        dc = Integer.parseInt(dcStr, 16);
        value = Integer.parseInt(valueStr,16);
        time = Integer.parseInt(timeStr, 16);
        try {
            name = new String(HexUtil.hexStringToBytes(nameStr), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getStatus() {
        return "红光";
    }

    public ArrayList<OscliiPoint> getEntry() {
        ArrayList<OscliiPoint> data = new ArrayList<>();
        OscliiPoint entry1 = new OscliiPoint(0, 0);
        OscliiPoint entry2 = new OscliiPoint(0,value*UNINT_VALUE);
        OscliiPoint entry3 = new OscliiPoint(dc*UNINT_DC,value*UNINT_VALUE);
        OscliiPoint entry4 = new OscliiPoint(dc*UNINT_DC,0);
        OscliiPoint entry5 = new OscliiPoint(dc*UNINT_DC+10,0);

        data.add(entry1);
        data.add(entry2);
        data.add(entry3);
        data.add(entry4);
        data.add(entry5);
        return data;
    }

    public String getDcDesc() {
        DecimalFormat format = new DecimalFormat("0.0");
        return format.format(dc*UNINT_DC);
    }

    public String getValueDesc() {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(value*UNINT_VALUE);
    }

    public boolean isLegal() {
        if(frequency>50000){
            illegalDesc = FR.str(R.string.illegal_frequcy);
            return false;
        }
        if(dc>100){
            illegalDesc = FR.str(R.string.illegal_dc);
            return false;
        }
        if(value>100){
            illegalDesc = FR.str(R.string.illegal_gonglv);
            return false;
        }
        return true;
    }

    public String getIllegalDesc() {
        return illegalDesc;
    }
}
