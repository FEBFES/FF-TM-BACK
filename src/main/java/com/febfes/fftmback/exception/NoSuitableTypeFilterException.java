package com.febfes.fftmback.exception;

import com.febfes.fftmback.domain.common.query.Operator;

import java.io.Serial;

public class NoSuitableTypeFilterException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2467263745000453160L;

    public NoSuitableTypeFilterException(Object value, Operator operator) {
        super("No suitable field type for value=" + value + " and operator=" + operator.name());
    }
}
