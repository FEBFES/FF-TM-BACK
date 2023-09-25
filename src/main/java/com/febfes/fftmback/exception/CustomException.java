package com.febfes.fftmback.exception;

import com.febfes.fftmback.dto.error.ErrorType;
import com.febfes.fftmback.dto.error.StatusError;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Map;

@Getter
@Setter
public abstract class CustomException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4326928128726471694L;

    private ErrorType errorType;
    private StatusError statusError;
    private Map<String, ?> baseError;

    public CustomException(String message, ErrorType errorType, StatusError statusError) {
        super(message);
        this.errorType = errorType;
        this.statusError = statusError;
    }
}
