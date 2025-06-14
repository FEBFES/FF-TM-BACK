package com.fftmback.authentication.exception;

import java.io.Serial;

public class TokenExpiredException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4361308075814580997L;

    private static final String DEFAULT_MESSAGE = "Access token has expired. Please make a new authenticate request";

    public TokenExpiredException(String token) {
        super(String.format("Failed for [%s]: %s", token, DEFAULT_MESSAGE));
    }
}
