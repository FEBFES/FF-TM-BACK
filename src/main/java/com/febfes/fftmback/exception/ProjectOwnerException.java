package com.febfes.fftmback.exception;

import java.io.Serial;

public class ProjectOwnerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3344216500301435093L;

    public ProjectOwnerException() {
        super("You cannot perform any actions with the owner in this project");
    }
}
