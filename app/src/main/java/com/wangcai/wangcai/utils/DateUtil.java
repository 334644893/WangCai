package com.wangcai.wangcai.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/3/7.
 */

public class DateUtil {
    public static final String DATE_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_2 = "yyyy-MM-dd";
    public static final String DATE_3 = "HH:mm:ss";

    /*
    * 将时间戳转换为时间type
    * 单位为秒
    */
    public static String stampToDate(String s, String type) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        long lt = new Long(s);
        Date date = new Date(lt * 1000);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
        * 将时间type转换为时间戳
        * 单位为秒
        */
    public static String dateToStamp(String s, String type) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(type);
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts / 1000);
        return res;
    }


    //获得当天0点时间 单位秒
    public static int getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis() / 1000);
    }

    //获得当天23点59分59秒时间 单位秒
    public static int getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) ((cal.getTimeInMillis() - 1000) / 1000);
    }

    //获得昨天0点时间 单位秒
    public static int getYesterdayTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) ((cal.getTimeInMillis() - 24 * 60 * 60 * 1000) / 1000);
    }

    //获得昨天23点59分59秒时间 单位秒
    public static int getYesterdayTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) ((cal.getTimeInMillis() - 1000) / 1000);
    }

    /**
     * start
     * 本周开始时间戳 - 以星期一为本周的第一天
     */
    public static String getWeekStartTime() {
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        cal.add(Calendar.DATE, -day_of_week + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() / 1000) + "";
    }

    /**
     * end
     * 本周结束时间戳 - 以星期一为本周的第一天
     */
    public static String getWeekEndTime() {
        Calendar cal = Calendar.getInstance();
        int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        cal.add(Calendar.DATE, -day_of_week + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return ((cal.getTimeInMillis() + 7 * 24 * 60 * 60 * 1000 - 1000) / 1000) + "";
    }
}
