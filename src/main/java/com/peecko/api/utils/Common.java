package com.peecko.api.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public abstract class Common {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String USER_DATE_FORMAT = "dd-MM-yyyy";

    public static final int MAX_DEVICES_ALLOWED = 3;

    private Common() {
       throw new IllegalStateException("Utility class");
    }

    public static String instantAsString(Instant time) {
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(time, zoneId);
        return localDateTime.format(formatter);
    }

    public static Instant endOfDay() {
        return LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
    }

    public static String localDateAsString(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atTime(LocalTime.MIDNIGHT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(USER_DATE_FORMAT);
        return localDateTime.format(formatter);
    }

    public static String lastDayOfMonthAsString() {
        LocalDate lastDayOfMonth = lastDayOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(USER_DATE_FORMAT);
        return lastDayOfMonth.format(formatter);
    }

    public static LocalDate lastDayOfMonth() {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        return yearMonth.atEndOfMonth();
    }



}
