package com.febfes.fftmback.exception;

import java.io.Serial;

public class RoleCheckException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1371235813489427594L;

    public RoleCheckException(String necessaryRole) {
        super("Required role for this action is %s".formatted(necessaryRole));
    }
}
