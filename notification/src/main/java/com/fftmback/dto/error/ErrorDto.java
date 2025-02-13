package com.fftmback.dto.error;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorDto(

        Integer statusCode,
        StatusError status,
        ErrorType errorType,
        LocalDateTime timestamp,
        String message,
        Map<String, ?> error
) {
}
