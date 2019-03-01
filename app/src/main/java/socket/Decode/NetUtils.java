package socket.Decode;


import com.canplay.milk.util.ConfigUtils;

import socket.LaserConstant;
import socket.laserprotocol.BaseLaserDate;
import socket.laserprotocol.Baseprotocol;
import socket.laserprotocol.LoginProtocol;
import socket.util.CRC16Modbus;
import socket.util.HexUtil;
import socket.util.WifiUtils;

/**
 * Created by linquandong on 16/12/5.
 */
public class NetUtils {
    public String macAddrss = "";
    private Baseprotocol mProtocol;
    private CRC16Modbus crc16Modbus;
    private BaseLaserDate mBaseDate;

    private static NetUtils instance;
    private StringBuffer mStrBuffer = new StringBuffer();

    private NetUtils() {
        macAddrss = WifiUtils.shareInstance().getMacAddress().replaceAll(":", "");
        mBaseDate = new BaseLaserDate();
        crc16Modbus = new CRC16Modbus();
    }

    public static NetUtils shareIntance() {
        if (instance == null) {
            instance = new NetUtils();
        }
        return instance;
    }

    public String getTranData(Baseprotocol baseprotocol) {
        //清空字符串。
        if (mStrBuffer.length() > 0) {
            mStrBuffer.delete(0, mStrBuffer.length());
        }
        mProtocol = baseprotocol;
        //头部
        String prefix = mProtocol.controlStr;
        String communicateStr = baseprotocol.makeSvrCmdStr();
        //如果是专线的指令
        if(communicateStr.startsWith(ConfigUtils.getRailwayFlag())){
            mStrBuffer.append(communicateStr);
        }else{
            mStrBuffer.append(prefix);
            mStrBuffer.append(communicateStr);
        }

        byte[] data = HexUtil.hexStringToBytes(mStrBuffer.toString().toUpperCase());
        //CRC校验
        String reuslt = crc16Modbus.getCrcHexStrReverse(data);
        mStrBuffer.append(reuslt);
        return mStrBuffer.toString();
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
        if (laserMsg.startsWith("03")) {
            mBaseDate.ret = 0;
            mBaseDate.nameStr = laserMsg.substring(2, 18);
            mBaseDate.controlStr = laserMsg.substring(18, 20);
            mBaseDate.lenStr = laserMsg.substring(20, 22);
            mBaseDate.busStr = laserMsg.substring(22, laserMsg.length() - 4);
            return mBaseDate;
        }

        //专线数据
        if(laserMsg.startsWith(ConfigUtils.getSubcribeFlag())){
            mBaseDate.ret = 0;
            String deviceName = ConfigUtils.getSubcribeFlag();
            mBaseDate.controlStr = LaserConstant.OPERATE;
            mBaseDate.busStr = laserMsg.substring(deviceName.length(), laserMsg.length() - 4);

            String contr = laserMsg.substring(deviceName.length(),deviceName.length()+2);
            //如果是申请控制帧
            if(contr.equals(LaserConstant.NETCONTROL)){
                mBaseDate.controlStr = LaserConstant.NETCONTROL;
                mBaseDate.lenStr = laserMsg.substring(deviceName.length()+2,deviceName.length()+4);
                mBaseDate.busStr = laserMsg.substring(deviceName.length()+4,laserMsg.length() - 4);
            }
            return mBaseDate;
        }

        //=====================业务====================
        mBaseDate.ret = 0;
        //2.获取控制位
        mBaseDate.controlStr = laserMsg.substring(0, 2);
        //4.业务数据
        mBaseDate.busStr = laserMsg.substring(2, laserMsg.length() - 4);

        //收到连接立刻发送登录请求
        if (mBaseDate.controlStr.equals(LaserConstant.LOGIN)) {
//            LoginProtocol.setRadomStr(mBaseDate.busStr);
        }

        return mBaseDate;
    }


    public boolean shouldSendmsg(BaseLaserDate data) {
        if (data.controlStr == null) {
            return false;
        }

        if (LaserConstant.ALLARG_S.equals(data.controlStr)) {
            return false;
        }

        if (LaserConstant.LOGIN.equals(data.controlStr)) {
            if (LoginProtocol.isLogining(data.busStr)) {
                return false;
            } else {
                return true;
            }
        }

        return true;
    }
}
