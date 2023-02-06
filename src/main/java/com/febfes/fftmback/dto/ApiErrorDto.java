package com.febfes.fftmback.dto;

import java.util.Date;
import java.util.List;

public record ApiErrorDto(
        Date timestamp,
        Integer status,
        List<String> errors,
        String message,
        String path
) {

}
