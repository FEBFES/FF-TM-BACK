package com.febfes.fftmback.exception;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.dto.error.ErrorType;
import com.febfes.fftmback.dto.error.StatusError;

import java.io.Serial;

import static com.febfes.fftmback.dto.error.RoleError.createBaseError;

public class RoleCheckException extends CustomException {

    @Serial
    private static final long serialVersionUID = -1371235813489427594L;

    public RoleCheckException(RoleName expected, RoleName actual) {
        super("Required role for this action is %s".formatted(expected.name()), ErrorType.ROLE, StatusError.ROLE_CHECK,
                createBaseError(expected, actual));
    }
}
