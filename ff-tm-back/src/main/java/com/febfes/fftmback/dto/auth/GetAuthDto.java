package com.febfes.fftmback.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record GetAuthDto (
    @Schema(description = "Access token. Lives 1 hour")
    String accessToken,

    @Schema(description = "Refresh token. Lives 24 hours")
    String refreshToken,

    Long userId
) {

}
