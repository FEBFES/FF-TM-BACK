package com.febfes.fftmback.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class DateProvider {

    public Date getCurrentDate() {
        return new Date();
    }

    public Date getCurrentDatePlusSeconds(int seconds) {
        return new Date(System.currentTimeMillis() + seconds * 1000L);
    }
}
