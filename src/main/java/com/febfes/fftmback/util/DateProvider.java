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

    public Date getCurrentDatePlusMs(int ms) {
        return new Date(System.currentTimeMillis() + ms);
    }
}
