package socket.laserprotocol.operate;


import com.canplay.milk.util.StringUtils;
import com.canplay.milk.view.OscliiChart.OscliiPoint;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import socket.LaserConstant;
import socket.laserprotocol.entry.RampField;
import socket.util.HexUtil;

/**
 * Created by linquandong on 17/2/6.
 */
public class Ramp extends Operate {
    public static  final float UINT_DC = 0.1f;

    public boolean isWrite;
    public String busId = LaserConstant.RAMP;
    public int sercode; //序号
    public String name;//程序名
    public int frequency;//频率
    public float dc;//占空比
    public String mcStyle;//脉冲模式
    public String mcSource;//脉冲来源
    public int repeCount;//重复次数
    public int start;//开始字段
    public int end;//结束字段
    public int fieldCount;//字段数
    public ArrayList<RampField> fields = new ArrayList<>();

    public Ramp(boolean switchSate) {
        this.isWrite = switchSate;

    }

    public Ramp(String laserData) {
        encode(laserData);
    }

    @Override
    public String makeCommunicateStr() {
        String hefix = isWrite ? HexUtil.or(busId, "80") : HexUtil.or(busId, "00");   //开红光命令 写

        String serCode = String.format("%02x", sercode); //1byte
        String data = null;
        if (isWrite) {
            String nameStr = StringUtils.rightPad(HexUtil.cn2Hex(name), 16, "20");//16进制20代表空格
            String frequencyStr = String.format("%04x", frequency);
            String dcStr = String.format("%04x", (int)(dc /UINT_DC));
            String mcStyleStr = "00";
            String mcSourceStr = "00";
            String repeStr = String.format("%04x",repeCount);
            String startStr = String.format("%02x",start);
            String endStr = String.format("%02x",end);
            data = serCode + nameStr + frequencyStr + dcStr + mcSourceStr + mcSourceStr+repeStr+startStr+endStr+getFileStr();
        } else {
            data = serCode;
        }
        return makCommnuByHeaderData(hefix, data);
    }

    private String getFileStr() {
        String fieldNumStr = String.format("%02x",fields.size());
        StringBuffer sb = new StringBuffer();
       for (int i = 0;i<fields.size();i++){
            sb.append(fields.get(i).makeCmdStr());
       }
        String filedStr = StringUtils.rightPad(sb.toString(),32*12,"0");
        return fieldNumStr+filedStr;
    }

    @Override
    public void encode(String laserData) {
        //TODO 解析laserData
        String serCodeStr = laserData.substring(0, 2);
        String nameStr = laserData.substring(2, 18);
        String frequencyStr = laserData.substring(18, 22);
        String dcStr = laserData.substring(22, 26);
        mcStyle = laserData.substring(26, 28);
        mcSource = laserData.substring(28, 30);
        String repeStr = laserData.substring(30, 34);
        String startStr = laserData.substring(34, 36);
        String endStr = laserData.substring(36, 38);
        String fieldCountStr = laserData.substring(38, 40);
        String filesStr = laserData.substring(40, laserData.length());

        sercode = Integer.parseInt(serCodeStr, 16);
        frequency = Integer.parseInt(frequencyStr, 16);
        dc = Integer.parseInt(dcStr, 16)*UINT_DC;
        try {
            name = new String(HexUtil.hexStringToBytes(nameStr), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        repeCount = Integer.parseInt(repeStr, 16);
        start = Integer.parseInt(startStr, 16);
        end = Integer.parseInt(endStr, 16);
        fieldCount = Integer.parseInt(fieldCountStr, 16);
        //解析字段
        encodeFileds(filesStr);
    }

    private void encodeFileds(String filesStr) {
        int index = 0;
        fields.clear();
        for (int i = 0; i < filesStr.length(); i += 12) {
            String grallyStr = filesStr.substring(i, i + 12);
            RampField field = new RampField(grallyStr, index);
            fields.add(field);
            index++;
            if (index == fieldCount) {
                break;
            }
        }
    }

    @Override
    public String getStatus() {
        return "Ramp";
    }

    public ArrayList<OscliiPoint> getEntry() {
        ArrayList<OscliiPoint> data = new ArrayList<>();
        int xindex = 0;
        //默认新坐标从0开始
        OscliiPoint entrystart = new OscliiPoint(xindex,fields.get(0).value);
        data.add(entrystart);

        for (int i = 0;i<fields.size();i++){
            xindex += fields.get(i).time;
            OscliiPoint entry = new OscliiPoint(xindex,fields.get(i).value);
            data.add(entry);
            if(i == end){
                xindex = addrepectEntry(data,xindex);
            }
        }
        return data;
    }
    //添加重复字段
    private int addrepectEntry(ArrayList<OscliiPoint> data, int xindex) {
        for (int i= 0;i<repeCount;i++){
            for (int j = start;j<=end;j++){
                xindex += fields.get(j).time;
                OscliiPoint entry = new OscliiPoint(xindex,fields.get(j).value);
                data.add(entry);
            }
        }
        return xindex;
    }

    public String getDcDesc() {
        DecimalFormat format = new DecimalFormat("0.0");
        return format.format(dc);
    }

//    public ArrayList<OscliiPoint> getEntry() {
//        ArrayList<OscliiPoint> data = new ArrayList<>();
//        OscliiPoint entry1 = new OscliiPoint(0, 0);
//        OscliiPoint entry2 = new OscliiPoint(0,value/100);
//        OscliiPoint entry3 = new OscliiPoint(dc/10,value/100);
//        OscliiPoint entry4 = new OscliiPoint(dc/10,0);
//        OscliiPoint entry5 = new OscliiPoint(value/100,0);
//
//        data.add(entry1);
//        data.add(entry2);
//        data.add(entry3);
//        data.add(entry4);
//        data.add(entry5);
//        return data;
//    }
}
