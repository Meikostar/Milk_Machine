package socket.laserprotocol.operate;


import com.canplay.milk.util.StringUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import socket.util.HexUtil;

/**
 * Created by linquandong on 17/2/6.
 */
public class LaserParam extends Operate {
    public static final int SHORT_TYPE = 1;
    public static final int FLOAT_TYPE = 2;
    public static final int STRING_TYPE = 3;
    public static final int HEX_TYPE = 4;
    public float uint = 0.1f;
    public String uintDesc = "";
    public boolean isWrite;
    public boolean wirteAble;//是否可写
    public String busId;//命令字
    //类型
    private float data; //数据
    private short shortData;//short类型的数据
    private String strData;//字符类型

    public int dataLen;//数据长度
    public int dataType;//数据类型 1：short 2：flat；3:string，4：直接截取16字符

    public String desc = "";//名称描述
    public String annotation = ""; //注释说明

    public int priority;//参数类型

    public LaserParam(boolean switchSate) {
        this.isWrite = switchSate;

    }

    public LaserParam(String laserData) {
        encode(laserData);
    }

    public LaserParam() {
    }

    @Override
    public String makeCommunicateStr() {
        String hefix = isWrite ? HexUtil.or(busId, "80") : HexUtil.or(busId, "00");
        String dataStr = null;
        String fromatStr = "%0" + dataLen + "x";
        if (isWrite) {
            dataStr = decode(fromatStr);
        } else {
            dataStr = String.format(fromatStr, 0);
        }
        return makCommnuByHeaderData(hefix, dataStr);
    }

    private String decode(String fromatStr) {
        switch (dataType) {
            case FLOAT_TYPE:
                int temData = data == 0 ? 0 : (int) (data / uint);
                return String.format(fromatStr, temData);
            case SHORT_TYPE:
                short temShortData = (short) (shortData == 0 ? 0 : (shortData / uint));
                return String.format(fromatStr, temShortData);
            case STRING_TYPE:
                String nameStr = StringUtils.rightPad(HexUtil.cn2Hex(strData), dataLen * 2, "20");//16进制20代表空格
                return nameStr;
//            case HEX_TYPE:
//                return
        }

        return null;
    }


    @Override
    public void encode(String laserData) {
        switch (dataType) {
            case SHORT_TYPE:
                shortData = (short) Integer.parseInt(laserData, 16);
                data = shortData * uint;
                break;
            case FLOAT_TYPE:
                data = Integer.parseInt(laserData, 16) * uint;
                break;
            case STRING_TYPE:
                try {
                    strData = new String(HexUtil.hexStringToBytes(laserData), "GBK");
                } catch (UnsupportedEncodingException e) {
                    strData = "";
                    e.printStackTrace();
                }
                break;
            case HEX_TYPE:
                strData = laserData;
                break;
        }

    }

    public static LaserParam formJson(JSONObject jsonObject) {
        LaserParam param = new LaserParam();
        int id = jsonObject.optInt("busId");
        param.busId = String.format("%02x",id).toUpperCase();

        param.priority = jsonObject.optInt("priority");
        param.uintDesc = jsonObject.optString("uintDesc");
        param.desc = jsonObject.optString("desc");
        param.annotation = jsonObject.optString("annotation");
        param.wirteAble = jsonObject.optBoolean("wirteAble");
        param.uint = (float) jsonObject.optDouble("uint");
        param.data = (float) jsonObject.optDouble("data");
        param.dataLen = jsonObject.optInt("dataLen");
        param.dataType = jsonObject.optInt("dataType");
        return param;
    }

    public String getIdDesc() {
        int id = Integer.parseInt(busId, 16);
        return String.format("%s%d", "ID：", id);
    }

    public String getValueDesc() {
        double value = 1 / uint;
        int log = (int) log(value, 10);
        switch (dataType) {
            case SHORT_TYPE:
            case FLOAT_TYPE:
                return String.format("%." + log + "f", data);
            case HEX_TYPE:
            case STRING_TYPE:
                return strData == null? "":strData.trim();

        }
        return "";
    }

    public void setValue(String value) {
        if(value.isEmpty()){
            return;
        }
        switch (dataType) {
            case SHORT_TYPE:
                shortData = (short) Integer.parseInt(value);
                break;
            case FLOAT_TYPE:
                data = Float.parseFloat(value);
                break;
            case HEX_TYPE:
            case STRING_TYPE:
                strData = value;
                break;
        }
    }

    public static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }


}
