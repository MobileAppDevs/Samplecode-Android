package com.bawa.inr.livechat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class ChatDateFormat {
    public static String generateTimestampInUTC() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simpleDateFormat.format(calendar.getTime());
    }


    public static String generateTimestampInUTC(String milies) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milies));
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simpleDateFormat.format(calendar.getTime());
    }

    public static String getRequiredFormat(String date, String newformat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String format = "";
        try {
            Date d = simpleDateFormat.parse(date);
            format = new SimpleDateFormat(newformat).format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return format;
    }

    public static Date getDateObject(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
