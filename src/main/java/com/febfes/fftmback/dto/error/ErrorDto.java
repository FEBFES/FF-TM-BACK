package com.febfes.fftmback.dto.error;

import java.util.Date;
import java.util.Map;

public record ErrorDto(

        Integer statusCode,
        StatusError status,
        ErrorType errorType,
        Date timestamp,
        String message,
        Map<String, ?> error
) {
}
