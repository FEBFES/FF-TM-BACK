package com.febfes.fftmback.exception;

import java.io.Serial;

public class EntityNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8185172189545821017L;

    public EntityNotFoundException(
            String entity,
            Long id
    ) {
        super("%s with id=%d not found".formatted(entity, id));
    }

    public EntityNotFoundException(
            String entity,
            String fieldName,
            String fieldValue
    ) {
        super("%s with %s=%s not found".formatted(entity, fieldName, fieldValue));
    }
}
