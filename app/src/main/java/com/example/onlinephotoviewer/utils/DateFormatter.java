package com.example.onlinephotoviewer.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Andrei on 30.03.2018.
 */

public class DateFormatter {

    private static final long CONST_DAYS = 86400000L;


    public static int dateToDays (Date date){
        long currentTime=date.getTime();
        currentTime=currentTime / CONST_DAYS;
        return (int) currentTime;
    }

    public static Date daysToDate(int days) {
        long currentTime=(long) days * CONST_DAYS;
        return new Date(currentTime);
    }

    public static int dateToTimestamp (Calendar c){
        long currentTime=c.getTimeInMillis();
        currentTime=currentTime / 1000;
        return (int) currentTime;
    }

    public static Date timestampToDate(int timestamp) {
        long currentTime=(long) timestamp * 1000;
        return new Date(currentTime);
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy",
                Locale.getDefault());
        return sdf.format(date);
    }
}
