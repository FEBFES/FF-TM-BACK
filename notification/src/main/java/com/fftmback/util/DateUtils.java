package com.fftmback.util;

import lombok.experimental.UtilityClass;

import java.time.Duration;
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

    public static Date getCurrentDatePlusDuration(Duration duration) {
        return new Date(System.currentTimeMillis() + duration.toMillis());
    }

    public static LocalDateTime getCurrentLocalDateTimePlusDuration(Duration duration) {
        return LocalDateTime.now().plus(duration);
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
