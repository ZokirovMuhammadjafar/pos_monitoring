package com.pos.monitoring.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    private static final Calendar calendar = Calendar.getInstance();
    private static final SimpleDateFormat FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    private static final String FROM_PREFIX = "T00:00:00";
    private static final String TO_PREFIX = "T23:59:59";

    public static String fromDate(Date from) {
        return FORMAT_YYYY_MM_DD.format(from) + FROM_PREFIX;
    }

    public static String toDate(Date from) {
        return FORMAT_YYYY_MM_DD.format(from) + TO_PREFIX;
    }

    public static Date minus(Date date, int param, int minus) {
        calendar.setTime(date);
        calendar.set(param, calendar.get(param) - minus);
        return calendar.getTime();
    }

    public static String toYYYYmmDD(Date date) {
        return FORMAT_YYYY_MM_DD.format(date);
    }
}
