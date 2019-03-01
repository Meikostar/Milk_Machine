package socket.Decode;

import socket.LaserConstant;
import socket.bean.MilkConstant;
import socket.laserprotocol.BaseLaserDate;
import socket.laserprotocol.Baseprotocol;
import socket.util.CRC16Modbus;
import socket.util.HexUtil;
import socket.util.WifiUtils;

/**
 * Created by linquandong on 16/12/5.
 */
public class LanUtils {
    public String macAddrss = "";
    private Baseprotocol mProtocol;
    private CRC16Modbus crc16Modbus;
    private BaseLaserDate mBaseDate;

    private static LanUtils instance;
    private LanUtils() {
        macAddrss = WifiUtils.shareInstance().getMacAddress().replaceAll(":", "");
        mBaseDate = new BaseLaserDate();
        crc16Modbus = new CRC16Modbus();
    }

    public static LanUtils shareIntance() {
        if (instance == null) {
            instance = new LanUtils();
        }
        return instance;
    }

    public String getTranData(Baseprotocol baseprotocol) {
        mProtocol = baseprotocol;
        //头部
        StringBuilder sb = new StringBuilder();
        sb.append(macAddrss.toUpperCase());

        sb.append(mProtocol.makeCommunicateStr().toUpperCase());
        byte[] data = HexUtil.hexStringToBytes(sb.toString());

        //CRC校验
        String reuslt = crc16Modbus.getCrcHexStr(data);
        sb.append(reuslt);
        return sb.toString();
    }

    public String getTranData(String  content) {

        //头部
        StringBuilder sb = new StringBuilder();
        return sb.toString();
    }
    public BaseLaserDate encodeDate(String laserMsg) {
        mBaseDate = new BaseLaserDate();
        //2.校验CRC
        if (!CheckoutUtils.rightlanCRC(laserMsg)) {
            mBaseDate.ret = 2;
            mBaseDate.errorDesc = "crc校验失败";
            return mBaseDate;
        }
        //通用状态
        if(laserMsg.startsWith("FFFFFF")){
            mBaseDate.ret = 0;
            mBaseDate.nameStr = laserMsg.substring(LaserConstant.DEVNAME_INDEX, LaserConstant.DEVNAME_INDEX+8*2);
            //3.获取控制位
            mBaseDate.controlStr = laserMsg.substring(LaserConstant.STATE_INDEX, LaserConstant.STATE_INDEX+2);
            //4.业务数据
            mBaseDate.lenStr = laserMsg.substring(LaserConstant.STATE_INDEX, LaserConstant.STATE_INDEX+2);
            mBaseDate.busStr = laserMsg.substring(LaserConstant.STATE_INDEX+4,laserMsg.length()-4);
            return  mBaseDate;
        }

        //=====================业务====================
        //1.验证mac地址
        if (!CheckoutUtils.isMacAddress(laserMsg)) {
            mBaseDate.ret = 1;
            mBaseDate.errorDesc = "mac地址不对应";
            return mBaseDate;
        }

        mBaseDate.ret = 0;

        mBaseDate.nameStr = laserMsg.substring(LaserConstant.DEVNAME_INDEX, LaserConstant.DEVNAME_INDEX+8*2);
        //3.获取控制位
        mBaseDate.controlStr = laserMsg.substring(LaserConstant.CONTOR_INDEX, LaserConstant.CONTOR_INDEX+2);

        //4.业务数据
        mBaseDate.lenStr = laserMsg.substring(LaserConstant.LEN_INDEX, LaserConstant.LEN_INDEX+2);
        mBaseDate.busStr = laserMsg.substring(LaserConstant.BUS_INDEX,laserMsg.length()-4);
        return  mBaseDate;
    }


    public boolean shouldSendmsg(BaseLaserDate data) {
        if(data.controlStr == null){
            return false;
        }
        //当时状态广播帧的时候返回false ，避免覆盖发送出去的操作指令
        if(LaserConstant.ALLARG_S.equals(data.controlStr)){
            return false;
        }

        return true;
    }
}
