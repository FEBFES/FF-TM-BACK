package com.fftmback.authentication.exception;

import com.fftmback.authentication.dto.error.ErrorType;
import com.fftmback.authentication.dto.error.StatusError;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class CustomException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4326928128726471694L;

    private final ErrorType errorType;
    private final StatusError statusError;
    private final transient Map<String, Object> baseError;

    protected CustomException(String message, ErrorType errorType, StatusError statusError) {
        super(message);
        this.errorType = errorType;
        this.statusError = statusError;
        this.baseError = new HashMap<>();
    }

    protected CustomException(
            String message,
            ErrorType errorType,
            StatusError statusError,
            Map<String, Object> baseError
    ) {
        super(message);
        this.errorType = errorType;
        this.statusError = statusError;
        this.baseError = baseError;
    }
}
