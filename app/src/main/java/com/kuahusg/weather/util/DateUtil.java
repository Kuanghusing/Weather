package com.kuahusg.weather.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kuahusg on 16-6-29.
 */

public class DateUtil {
    private static Date date;
    private static SimpleDateFormat format;
    public static String getDate(String formatString){
        date = new Date();
        DateUtil.format = new SimpleDateFormat(formatString);
        return format.format(date);
    }

    public static String getDate(String formatString, int howLongFromToday) {
        date = new Date();
        DateUtil.format = new SimpleDateFormat(formatString);
        return format.format(new Date(date.getTime() - (howLongFromToday * 24 * 60 * 60 * 1000)));

    }


    public static int getDatePart(int field,int offSet) {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(field) - offSet;

    }

   /* public static String getFormatDate(String formatString, String date_string) {
        date = new Date(date_string);
        format = new SimpleDateFormat(formatString);
        return format.format(date);

    }*/
}
