package com.febfes.fftmback.exception;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(
            String entity,
            String fieldName,
            String fieldValue
    ) {
        super("%s with %s=%s already exists");
    }
}
