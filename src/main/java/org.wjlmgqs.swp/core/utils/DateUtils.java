package org.wjlmgqs.swp.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    //一天的最后一秒前：23:59:59.999
    public static final int DAY_SECOND = 1 * 24 * 60 * 60 * 1000 - 1;

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date diff(Date date, long m) {
        Date result = new Date();
        result.setTime(date.getTime() - m);
        return result;
    }

    public static Date dayMax(Date date) {
        Date min = dayMin(date);
        min.setTime(min.getTime() + DAY_SECOND);
        return min;
    }

    public static Date dayMin(Date date) {
        String formatDay = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD).format(date) + " 00:00:00";
        Date maxTime = null;
        try {
            maxTime = new SimpleDateFormat(DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).parse(formatDay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxTime;
    }

    public static Date parse2Date(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
