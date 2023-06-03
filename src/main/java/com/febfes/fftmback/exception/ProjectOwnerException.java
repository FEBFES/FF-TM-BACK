package com.febfes.fftmback.exception;

import java.io.Serial;

public class ProjectOwnerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 3344216500301435093L;

    public ProjectOwnerException() {
        super("Owner can't be added to project members");
    }

    public ProjectOwnerException(Long expectedOwnerId) {
        super("Only the user with id=%d can add a new member to the project".formatted(expectedOwnerId));
    }
}
