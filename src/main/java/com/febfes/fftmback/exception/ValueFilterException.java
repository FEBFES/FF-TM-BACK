package com.febfes.fftmback.exception;

import java.io.Serial;
import java.util.List;

public class ValueFilterException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1921061725988705200L;

    public ValueFilterException(List<Object> values) {
        super("All values must belong to the same class: %s".formatted(values));
    }

    public ValueFilterException(Object value, Object valueTo) {
        super("value=%s and valueTo=%s must belong to the same class".formatted(value, valueTo));
    }
}
