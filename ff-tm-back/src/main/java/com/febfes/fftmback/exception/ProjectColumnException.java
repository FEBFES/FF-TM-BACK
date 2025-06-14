package com.febfes.fftmback.exception;

import java.io.Serial;

public class ProjectColumnException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1385399153343214035L;

    public ProjectColumnException(Long projectId, Long columnId) {
        super("Project with id=%d doesn't contain column with id=%d".formatted(projectId, columnId));
    }
}
