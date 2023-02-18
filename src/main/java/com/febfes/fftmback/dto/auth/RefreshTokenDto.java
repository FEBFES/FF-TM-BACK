package com.febfes.fftmback.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import static com.febfes.fftmback.config.jwt.JwtAuthenticationFilter.BEARER;

public record RefreshTokenDto(

        @Schema(description = "Access token. Lives 1 hour")
        String accessToken,

        @Schema(description = "Refresh token. Lives 24 hours")
        String refreshToken,

        @Schema(description = "Token type. Bearer")
        String tokenType
) {
    public RefreshTokenDto(String accessToken, String refreshToken) {
        this(accessToken, refreshToken, BEARER);
    }
}
