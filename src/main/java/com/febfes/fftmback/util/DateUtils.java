package com.febfes.fftmback.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@UtilityClass
public class DateUtils {

    public static Date getCurrentDate() {
        return new Date();
    }

    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }

    public static Date getCurrentDatePlusSeconds(int seconds) {
        return new Date(System.currentTimeMillis() + seconds * 1000L);
    }

    public static LocalDateTime getCurrentLocalDateTimePlusSeconds(int seconds) {
        return LocalDateTime.now().plusSeconds(seconds);
    }

    public static boolean isDateBeforeCurrentDate(Date date) {
        return date.before(DateUtils.getCurrentDate());
    }

    public static boolean isDateBeforeCurrentDate(LocalDateTime date) {
        return date.isBefore(DateUtils.getCurrentLocalDateTime());
    }

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date convertLocalDateTimeToDate(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }
}
