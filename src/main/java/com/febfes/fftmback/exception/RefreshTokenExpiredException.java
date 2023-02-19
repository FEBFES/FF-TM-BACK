package com.febfes.fftmback.exception;

import java.io.Serial;

public class RefreshTokenExpiredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4361308075814580997L;

    private static final String DEFAULT_MESSAGE = "Refresh token has expired. Please make a new authenticate request";

    public RefreshTokenExpiredException(String token) {
        super(String.format("Failed for [%s]: %s", token, DEFAULT_MESSAGE));
    }
}
