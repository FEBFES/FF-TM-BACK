package com.febfes.fftmback.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@UtilityClass
public class DateUtils {
    public static final String STANDARD_DATE_PATTERN = "dd-MM-yyyy HH:mm:ss";

    public static Date getCurrentDate() {
        return new Date();
    }

    public static Date getCurrentDatePlusSeconds(int seconds) {
        return new Date(System.currentTimeMillis() + seconds * 1000L);
    }

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date convertLocalDateTimeToDate(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
