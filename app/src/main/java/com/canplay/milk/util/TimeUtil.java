package com.canplay.milk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/***
 * 功能描述:
 * 作者:qiujialiu
 * 时间:2016/12/20
 * 版本:1.0
 ***/

public class TimeUtil {

    private static String mYear;
    private static String mMonth;
    private static String mDay;
    private static String mWay;

    public static String StringData(long currentTime ){

        Date dateOld = new Date(currentTime);
        final Calendar c = Calendar.getInstance();
        c.setTime(dateOld);
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return mYear + "年" + mMonth + "月" + mDay+"日  "+"星期"+mWay;
    }
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }
    /*将字符串转为时间戳*/
 public static long getStringToDate(String time) {
     SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try{
            date = sdf.parse(time);
            } catch(ParseException e) {
            // TODO Auto-generated catch block
           e.printStackTrace();
           }
        return date.getTime();
       }
    public static String formatHour(long time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }
    public static String formatToFileName(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        return format.format(new Date(time));
    }
    public static String formatToName(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(time));
    }
    public static String formatTime(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        return format.format(new Date(time));
    }
    public static String formatTims(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(new Date(time));
    }
    public static String formatTimss(long time){
        SimpleDateFormat format = new SimpleDateFormat("MM.dd  HH:mm");
        return format.format(new Date(time));
    }
    public static String formatTimes(long time){
        SimpleDateFormat format = new SimpleDateFormat("MM.dd-HH:mm");
        return format.format(new Date(time));
    }
    public static String formatToMs(long time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }
    public static String formatToMf(long time){
        SimpleDateFormat format = new SimpleDateFormat("MM.dd ## HH:mm:ss");
        return format.format(new Date(time));
    }
    public static String formatToMD(long time){
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日");
        return format.format(new Date(time));
    }
    public static String formatToMDs(long time){
        SimpleDateFormat format = new SimpleDateFormat("MM#dd");
        return format.format(new Date(time));
    }
    public static String format(long time,String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(new Date(time));
    }

    public static String getCurrentTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }
    public static String formatToTipsTime(String space,String time)
    {
        String result="";

        if(TextUtil.isNotEmpty(time)){

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try
            {
                Date date=format.parse(time);

                result=formatToString(space,date);

                return result;

            } catch (ParseException e) {

                Date date=new Date();

                result=date.getHours()+":"+date.getMinutes();

                return result;
            }

        }else{

            Date date=new Date();

            result=date.getHours()+":"+date.getMinutes();

            return result;
        }
    }
    public static boolean isToday(String time)
    {
        boolean result=false;

        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            long create = sdf.parse(time).getTime();

            Calendar now = Calendar.getInstance();

            long ms  = 1000*(now.get(Calendar.HOUR_OF_DAY)*3600+now.get(Calendar.MINUTE)*60+now.get(Calendar.SECOND));//毫秒数

            long ms_now = now.getTimeInMillis();

            if(ms_now-create<ms)
                result=true;
        }
        catch (Exception e)
        {
            result=false;
        }

        return result;
    }
    public static String parseDate(String createTime){

        try {

            String ret = "";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            long create = sdf.parse(createTime).getTime();

            Calendar now = Calendar.getInstance();

            long ms  = 1000*(now.get(Calendar.HOUR_OF_DAY)*3600+now.get(Calendar.MINUTE)*60+now.get(Calendar.SECOND));//毫秒数

            long ms_now = now.getTimeInMillis();

            if(ms_now-create<ms){
                ret = "今天";
            }else if(ms_now-create<(ms+24*3600*1000)){
                ret = "昨天";
            }else if(ms_now-create<(ms+24*3600*1000*2)){
                ret = "前天";
            }else{
                ret= "更早";
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 消息创建时间距离现在时间：
     1.	60s内，显示【刚刚】
     2.	超出1分钟小于1小时，显示【xx分钟前】
     3.	超出1小时并且还是当天，显示【xx小时前】，如果不是当天，显示【昨天 ss:mm】
     4.	不是当天消息，昨天消息，显示【昨天 ss:mm】
     5.	不是当天消息，前天消息，显示【前天 ss:mm】
     6.	超出前天消息，并且在本月内，显示【xx天前】
     7.	超出30天消息，并且不在本月内，显示【xx 个月前】
     8.	超出12个月，并且不再本年内，显示【xx 年前】
     * */
    private static String formatToString(String space,Date date){
        long current = System.currentTimeMillis();
        long thisDate = date.getTime();
        if (current>=thisDate) {
            List<String> currentList = judgeDate(current);
            List<String> thisList = judgeDate(thisDate);
            //LogUtils.w(TAG,"current - old :"+(current-thisDate)+"====="+((1000*60*60*24*365)) +"=====" +((current-thisDate)-(1000*60*60*24*365)));
            if (current-thisDate>(1000L*60*60*24*365)
                    &&(!currentList.get(0).equals(thisList.get(0)))) {
                int y = Integer.parseInt(currentList.get(0)) - Integer.parseInt(thisList.get(0));
                return y + "年前";
            }else if ((current-thisDate)>(1000L*60*60*24*30)
                    &&(!currentList.get(1).equals(thisList.get(1)))){
                int M = Integer.parseInt(currentList.get(1)) - Integer.parseInt(thisList.get(1));
                return ((M+12)%12) + "个月前";
            }else if (getBetweenDay(current,thisDate)>2){
                int d = getBetweenDay(current,thisDate);
                return d + "天前";
            }else if (getBetweenDay(current,thisDate)==2){
                return "前天"+space+thisList.get(3)+":"+thisList.get(4);
            }else if (getBetweenDay(current,thisDate)==1){
                return "昨天"+space+thisList.get(3)+":"+thisList.get(4);
            }else if (getBetweenDay(current,thisDate)==0 && current-thisDate>=(1000*60*60)){
                int H = Integer.parseInt(currentList.get(3)) - Integer.parseInt(thisList.get(3));
                return ((H+24)%24)+"小时前";
            }else if (current-thisDate>60000 && current-thisDate<(1000*60*60)){
                int m = (int) ((current-thisDate)/(1000*60));
                return m+"分钟前";
            }else {
                return "刚刚";
            }
        }else {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            return  sdf.format(date);
        }
    }

    /**
     * 得到两个日期相差的天数
     */
    public static int getBetweenDay(long date1, long date2) {

        Calendar d1 = Calendar.getInstance();
        Calendar d2 = Calendar.getInstance();
        if (date1>date2){
            d1.setTimeInMillis(date2);
            d2.setTimeInMillis(date1);
        }
        int day1 = d1.get(Calendar.DAY_OF_YEAR);
        int day2 = d2.get(Calendar.DAY_OF_YEAR);
        int y1 = d1.get(Calendar.YEAR);
        int y2 = d2.get(Calendar.YEAR);
        if (y1 == y2){
            return day2 - day1;
        }else {
            return (int) Math.ceil((Math.abs(date2-date1)/(1000*60*60*24)));
        }
    }

    /**
     * 由长整形的时间戳得到年月日
     *
     * @param time
     *            　长整时间戳
     * @return　年月日的list集合
     */
    public static List<String> judgeDate(Long time) {
        List<String> strings = new ArrayList<String>();
        String hourString = null;
        String minuteString = null;
        String yearString = null;
        String monthString = null;
        String dayString = null;
        String re_StrTime = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        re_StrTime = sdf.format(new Date(time));
        hourString = re_StrTime.substring(11, 13);
        minuteString = re_StrTime.substring(14, 16);
        yearString = re_StrTime.substring(0, 4);
        monthString = re_StrTime.substring(5, 7);
        dayString = re_StrTime.substring(8, 10);
        if((Integer.parseInt(hourString)+8)>24){
            int day = Integer.parseInt(dayString)+1;
            dayString = Integer.toString(day);
        }
        strings.add(yearString);
        strings.add(monthString);
        strings.add(dayString);
        strings.add(hourString);
        strings.add(minuteString);
        return strings;
    }

    /**
     * 获取当前日期是星期几
     *
     * @return
     */
    public static String getDayWeek(Date date) {
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];

    }




}
