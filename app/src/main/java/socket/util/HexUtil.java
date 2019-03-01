
package socket.util;

import java.io.UnsupportedEncodingException;

/**
 * reference apache commons <a
 * href="http://commons.apache.org/codec/">http://commons.apache.org/codec/</a>
 *
 * @author Aub
 */
public class HexUtil {
    private static String hexString = "0123456789ABCDEF";
    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data byte[]
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data        byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制char[]
     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * 将字节数组转换为十六进制字符数组
     *
     * @param data     byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制char[]
     */
    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data byte[]
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data        byte[]
     * @param toLowerCase <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式
     * @return 十六进制String
     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param data     byte[]
     * @param toDigits 用于控制输出的char[]
     * @return 十六进制String
     */
    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }
    /**
     * 将16进制字符串转换为byte数组
     *
     * @param
     */
    public static byte[] hexStringToByte(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.trim().replaceAll("\\s*", ""); // 去除字符串中的空格

        String hexFormat = "0123456789ABCDEF";

        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (hexFormat.indexOf(hexChars[pos]) << 4 | hexFormat
                    .indexOf(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * 将byte数组转化为16进制字符串
     */
    public static String bytes2HexString(byte[] bytes) {
        int len = bytes.length;
        StringBuffer sb = new StringBuffer();
        char temp;
        for (int i = 0; i < len; i++) {
            temp = (char) ((bytes[i] >> 4) & 0x0F);
            sb.append((char) (temp > 9 ? (temp + 'A' - 10) : temp + '0'));
            temp = (char) (bytes[i] & 0x0F);
            sb.append((char) (temp > 9 ? (temp + 'A' - 10) : temp + '0'));
        }
        return sb.toString();
    }
    /**
     * 将十六进制字符数组转换为字节数组
     *
     * @param data 十六进制char[]
     * @return byte[]
     * @throws RuntimeException 如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常
     */
    public static byte[] decodeHex(char[] data) {

        int len = data.length;

        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    /**
     * 将十六进制字符转换成一个整数
     *
     * @param ch    十六进制char
     * @param index 十六进制字符在字符数组中的位置
     * @return 一个整数
     * @throws RuntimeException 当ch不是一个合法的十六进制字符时，抛出运行时异常
     */
    public static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }

    public static int hexToInt(byte data) {
        String hexStr = Integer.toHexString(data & 0xFF);
        return Integer.parseInt(hexStr, 16);
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            b[i] = (byte) (toByte(hexChars[pos]) << 4 | toByte(hexChars[pos + 1]));
        }
        return b;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    //
    public static String xor(String strHex_X, String strHex_Y){
        //将x、y转成二进制形式
        String anotherBinary=mutilHexStringToBitNoReverse(strHex_X);
        String thisBinary=mutilHexStringToBitNoReverse(strHex_Y);
        String result = "";

        //异或运算
        for(int i=0;i<anotherBinary.length();i++){
            //如果相同位置数相同，则补0，否则补1
            if(thisBinary.charAt(i)==anotherBinary.charAt(i))
                result+="0";
            else{
                result+="1";
            }
        }
        return Integer.toHexString(Integer.parseInt(result, 2));
    }
    public static String or(String strHex_X, String strHex_Y){
        //将x、y转成二进制形式
        String anotherBinary=mutilHexStringToBitNoReverse(strHex_X);
        String thisBinary=mutilHexStringToBitNoReverse(strHex_Y);
        String result = "";

        //或运算
        for(int i=0;i<anotherBinary.length();i++){
            if(thisBinary.charAt(i)== 49||anotherBinary.charAt(i)==49)
                result+="1";
            else{
                result+="0";
            }
        }
        return String.format("%02x", Integer.parseInt(result, 2));
    }
    public static String and(String strHex_X, String strHex_Y){
        //将x、y转成二进制形式
        String anotherBinary=mutilHexStringToBitNoReverse(strHex_X);
        String thisBinary=mutilHexStringToBitNoReverse(strHex_Y);
        String result = "";

        //与运算
        for(int i=0;i<anotherBinary.length();i++){
            //如果相同位置数相同，则补0，否则补1
            int charleft = anotherBinary.charAt(i);
            if(thisBinary.charAt(i) == 49 && anotherBinary.charAt(i)==49)
                result+="1";
            else{
                result+="0";
            }
        }
        return Integer.toHexString(Integer.parseInt(result, 2));
    }

    /**
     * 将hex字符转换成二进制，并进行倒序，使得高位在后
     * @param strHex
     * @return
     */
    public static String mutilHexStringToBitStr(String strHex) {
        byte [] bytes = hexStringToBytes(strHex);
        StringBuffer sb = new StringBuffer();
        for (int i = 0;i<bytes.length;i++){
            sb.append(byteToBit(bytes[i]));
        }
        //倒序输出，使得高位在后
        return sb.reverse().toString();
    }
    /**
     * 将hex字符转换成二进制
     * @param strHex
     * @return
     */
    public static String mutilHexStringToBitNoReverse(String strHex) {
        byte [] bytes = hexStringToBytes(strHex);
        StringBuffer sb = new StringBuffer();
        for (int i = 0;i<bytes.length;i++){
            sb.append(byteToBit(bytes[i]));
        }
        //倒序输出，使得高位在后
        return sb.toString();
    }

    /**
     * Byte转Bit
     */
    public static String byteToBit(byte b) {
        return "" +(byte)((b >> 7) & 0x1) +
                (byte)((b >> 6) & 0x1) +
                (byte)((b >> 5) & 0x1) +
                (byte)((b >> 4) & 0x1) +
                (byte)((b >> 3) & 0x1) +
                (byte)((b >> 2) & 0x1) +
                (byte)((b >> 1) & 0x1) +
                (byte)((b >> 0) & 0x1);
    }

    public static String hexStringToBitStr(String strHex) {
        long data = Long.valueOf(strHex,16);
       String bitStr =  Long.toBinaryString(data);
        //判断是否为8位二进制，否则左补零
        if(bitStr.length() != 8){
            for (int i = bitStr.length(); i <8; i++) {
                bitStr = "0"+bitStr;
            }
        }
        return bitStr;
    }

//
//

    /**
     * ===================================
     // 汉字通过gbk 转成16进制       start
     * @param targetStr
     * @return
     * ===================================
     */
    public static String cn2Hex(String targetStr) {
    StringBuilder hexStr = new StringBuilder();
    int len = targetStr.length();
    if (len > 0) {
        for (int i = 0; i < len; i++) {
            char tempStr = targetStr.charAt(i);
            String data = String.valueOf(tempStr);
            if (isCN(data)) {
                hexStr.append(encodeCN(data));
            } else {
                hexStr.append(encodeStr(data));
            }
        }
    }
    return hexStr.toString();
}
    public static String encodeCN(String data) {
        byte[] bytes;
        try {
            bytes = data.getBytes("gbk");
            StringBuilder sb = new StringBuilder(bytes.length * 2);

            for (int i = 0; i < bytes.length; i++) {
                sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
                sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String encodeStr(String data) {
        String result = "";
        byte[] bytes;
        try {
            bytes = data.getBytes("gbk");
            for (int i = 0; i < bytes.length; i++) {
                result += Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 判定是否为中文汉字
     * @param data
     * @return
     */
    public static boolean isCN(String data) {
        boolean flag = false;
        String regex = "^[\u4e00-\u9fa5]*$";
        if (data.matches(regex)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 把16进制的转gbk编码
     * @param str
     * @return
     */
    public static String hexStr2CN(String str) {
        try {
            return new String(hexStringToBytes(str), "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

}
