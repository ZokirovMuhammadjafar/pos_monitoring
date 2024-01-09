package com.pos.monitoring.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    // Formatter with the desired pattern
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String format(LocalDateTime from) {
        return formatter.format(from);
    }

    public static String format(LocalDate from) {
        return dateFormat.format(from);
    }

    public static Date yesterday() {
        LocalDate localDate = LocalDate.now().minusDays(1);
        // Convert LocalDate to Date
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return date;
    }
}
