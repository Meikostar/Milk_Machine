package socket.Decode;

import socket.util.CRC16Modbus;
import socket.util.HexUtil;
import socket.util.WifiUtils;

/**
 * Created by linquandong on 16/12/9.
 * 用于校验mac，和数据的完整性，cRc
 */
public class CheckoutUtils {

    //判定mac地址
    public static boolean isMacAddress(String laserMsg) {
        String myMac = WifiUtils.shareInstance().getMacAddress().replaceAll(":", "").toUpperCase();
        return laserMsg.startsWith(myMac);
    }

    public static boolean rightCRC(String laserMsg) {
        try {

            String crc = laserMsg.substring(laserMsg.length()-4,laserMsg.length());
            //
            String hexStr = laserMsg.substring(0,laserMsg.length()-4);
            byte[] date = HexUtil.hexStringToBytes(hexStr);
            String makeCrc = new CRC16Modbus().getCrcHexStr(date);
            return makeCrc.equals(crc);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 需要调转位置
     * @param laserMsg
     * @return
     */
    public static boolean rightlanCRC(String laserMsg) {
        try {

            String crc = laserMsg.substring(laserMsg.length()-4,laserMsg.length());
            //
            String reverseCrc = crc.substring(2)+crc.substring(0,2);
            String hexStr = laserMsg.substring(0,laserMsg.length()-4);
            byte[] date = HexUtil.hexStringToBytes(hexStr);
            String makeCrc = new CRC16Modbus().getCrcHexStr(date);
            return makeCrc.equals(reverseCrc);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
