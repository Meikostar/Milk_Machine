package com.canplay.milk.util;

import android.text.Html;
import android.text.Spanned;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;


public class StringUtils {

    public static final String RMB = "￥";

    public static boolean isEmpty(String string) {
        boolean ret = true;
        if (string != null && string.length() != 0) {
            //有一个字符不为空，那么整个字符串就非空了
            for (int i = 0; i < string.length(); i++) {
                if (!Character.isWhitespace(string.charAt(i))) {
                    ret = false;
                    break;
                }
            }
        }
        return ret;
    }

    public static String getMD5(String source) {
        String s = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};// 用来将字节转换成16进制表示的字符
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                    .getInstance("MD5");
            md.update(source.getBytes());
            byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，  
            // 用字节表示就是 16 个字节  
            char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16  
            // 进制需要 32 个字符  
            int k = 0;// 表示转换结果中对应的字符位置  
            for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16  
                // 进制字符的转换  
                byte byte0 = tmp[i];// 取第 i 个字节  
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>  
                // 为逻辑右移，将符号位一起右移  
                str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换  

            }
            s = new String(str);// 换后的结果转换为字符串

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    public static String[] convertStrToArray(String update_mode) {
        String[] versionStrings = update_mode.split(",");
        return versionStrings;
    }

    public static String double2Moeny(double price) {
        return new String().format("%s%.0f", RMB, price);
    }

    public static String double2(double price) {
        return new String().format("%.2f", price);
    }

    public static String double2NoDecimal(double price) {
        return new String().format("%.0f", price);
    }

    public static String double2MoneyFormat(double price) {
        return new String().format("%s%.0f", RMB, price);
    }

    public static boolean isNumeric(String str) {
        if (str.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }


    public static Spanned getDefaultColorStr(String prefix, String end){
        return getColorText(0x777777,prefix,0xfb7007,end);
    }
    /**
     * 不同颜色文本
     *
     * @param colorL
     * @param txtL
     * @param colorR
     * @param txtR
     * @return
     */
    public static Spanned getColorText(int colorL, String txtL, int colorR, String txtR) {
        String str = "<font color=\'" + colorL + "\'>" + txtL + "</font><font color=\'" + colorR + "\'>" + txtR + "</font>";
        return Html.fromHtml(str);
    }

    public static Spanned getDefaultColorStr(String prefix, String mid, String end){
       return getColorText(0x777777,prefix,0xfb7007,mid,0x777777,end);
    }

    public static Spanned getColorText(int colorL, String txtL, int colorR, String txtR, int colorLast, String last) {
        String str = "<font color=\'" + colorL + "\'>" + txtL + "</font>" +
                    "<font color=\'" + colorR + "\'>" + txtR + "</font>" +
                    "<font color=\'" + colorLast + "\'>" + last + "</font>"
                ;
        return Html.fromHtml(str);
    }

    public static Spanned getMinGoodsText(String minNum){
        String str = "<font color=\'" +0x777777 + "\'>" + "购买数量" + "</font>" +
                "<font color=\'" +  0xfe4848 + "\'>" + minNum + "</font>"+
                "<font color=\'" +  0x777777 + "\'>" + "件起购" + "</font>";

        return Html.fromHtml(str);
    }

    public static double getResetMoney(double txt_money, double percent) {
       txt_money = txt_money - txt_money*percent/100;
        return txt_money;
    }

    public static String rightPad(String name, int lenght, String s) {
        int count = (lenght - name.length())/s.length();
        if(count>0){
            for (int i = 0;i<count;i++){
                name += s;
            }
        }
        return name;
    }

    public static String adapterRight(String name, int len, String holderStr){
        if(name.length()>len){
            return name.substring(0,len);
        }else{
            return rightPad(name,len,holderStr);
        }
    }
}
