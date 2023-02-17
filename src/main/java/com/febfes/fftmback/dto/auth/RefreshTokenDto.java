package com.febfes.fftmback.dto.auth;

import static com.febfes.fftmback.config.jwt.JwtAuthenticationFilter.BEARER;

public record RefreshTokenDto(

        String accessToken,

        String refreshToken,

        String tokenType
) {
    public RefreshTokenDto(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, BEARER);
    }
}
