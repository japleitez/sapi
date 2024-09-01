package com.peecko.api.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public abstract class Common {

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final int MAX_DEVICES_ALLOWED = 3;
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String instantAsString(Instant time) {
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(time, zoneId);
        return localDateTime.format(formatter);
    }

    public static String localDateAsString(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atTime(LocalTime.MIDNIGHT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        return localDateTime.format(formatter);
    }

    public static Instant endOfDay() {
        return LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
    }

    public static int currentPeriod() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        return year * 100 + month;
    }

    public static int previousPeriod() {
        LocalDate today = LocalDate.now();
        LocalDate previousMonth = today.minusMonths(1);
        return previousMonth.getYear() * 100 + previousMonth.getMonthValue();
    }

    public static LocalDate lastDayOfMonth() {
        LocalDate today = LocalDate.now();
        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonth());
        return yearMonth.atEndOfMonth();
    }

    public static String lastDayOfMonthAsString() {
        LocalDate lastDayOfMonth = lastDayOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return lastDayOfMonth.format(formatter);
    }

}
