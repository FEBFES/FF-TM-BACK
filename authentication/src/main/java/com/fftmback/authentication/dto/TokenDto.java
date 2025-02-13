package com.fftmback.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenDto(

        @Schema(description = "Access token. Lives 1 hour")
        String accessToken,

        @Schema(description = "Refresh token. Lives 24 hours")
        String refreshToken
) {
}
