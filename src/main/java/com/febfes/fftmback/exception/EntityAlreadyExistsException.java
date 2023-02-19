package com.febfes.fftmback.exception;

import java.io.Serial;

public class EntityAlreadyExistsException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5420486842398483284L;

    public EntityAlreadyExistsException(String entity) {
        super("%s already exists".formatted(entity));
    }

    public EntityAlreadyExistsException(
            String entity,
            String fieldName,
            String fieldValue
    ) {
        super("%s with %s=%s already exists".formatted(entity, fieldName, fieldValue));
    }
}
