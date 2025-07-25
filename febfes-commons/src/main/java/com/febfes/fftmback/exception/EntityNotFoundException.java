package com.febfes.fftmback.exception;

import com.febfes.fftmback.dto.ErrorType;
import com.febfes.fftmback.dto.StatusError;
import lombok.Getter;

import java.io.Serial;

import static com.febfes.fftmback.dto.AuthError.createBaseError;

@Getter
public class EntityNotFoundException extends CustomException {

    private static final StatusError STATUS_ERROR = StatusError.NOT_FOUND;

    @Serial
    private static final long serialVersionUID = 8185172189545821017L;

    public EntityNotFoundException(
            String entity,
            Long id
    ) {
        super("%s with id=%d not found".formatted(entity, id), ErrorType.UNDEFINED, STATUS_ERROR);
    }

    public EntityNotFoundException(
            String entity,
            String fieldName,
            String fieldValue
    ) {
        super("%s with %s=%s not found".formatted(entity, fieldName, fieldValue), ErrorType.UNDEFINED, STATUS_ERROR);
    }

    public EntityNotFoundException(
            String entity,
            String fieldName,
            String fieldValue,
            ErrorType errorType
    ) {
        super("%s with %s=%s not found".formatted(entity, fieldName, fieldValue), errorType, STATUS_ERROR,
                createBaseError(entity, fieldName, fieldValue, errorType));
    }
}
