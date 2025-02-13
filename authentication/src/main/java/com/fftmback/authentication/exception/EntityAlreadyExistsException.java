package com.fftmback.authentication.exception;


import com.fftmback.authentication.dto.error.ErrorType;
import com.fftmback.authentication.dto.error.StatusError;

import java.io.Serial;

import static com.fftmback.authentication.dto.error.AuthError.createBaseError;


public class EntityAlreadyExistsException extends CustomException {

    private static final StatusError STATUS_ERROR = StatusError.ENTITY_ALREADY_EXISTS;

    @Serial
    private static final long serialVersionUID = -5420486842398483284L;

    public EntityAlreadyExistsException(
            String entity,
            String fieldName,
            String fieldValue,
            ErrorType errorType
    ) {
        super("%s with %s=%s already exists".formatted(entity, fieldName, fieldValue), errorType, STATUS_ERROR,
                createBaseError(entity, fieldName, fieldValue, errorType));
    }
}
