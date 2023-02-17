package com.febfes.fftmback.exception;

public class RefreshTokenExpiredException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Refresh token has expired. Please make a new authenticate request";

    public RefreshTokenExpiredException(String token) {
        super(String.format("Failed for [%s]: %s", token, DEFAULT_MESSAGE));
    }
}
