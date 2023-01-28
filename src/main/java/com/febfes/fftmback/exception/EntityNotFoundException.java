package com.febfes.fftmback.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entity, Long id) {
        super("%s with id=%d not found".formatted(entity, id));
    }
}
