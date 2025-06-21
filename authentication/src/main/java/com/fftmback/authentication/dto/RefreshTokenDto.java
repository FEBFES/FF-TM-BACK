package com.fftmback.authentication.dto;

import java.time.LocalDateTime;

public record RefreshTokenDto(
        Long id,
        String token,
        LocalDateTime expiryDate,
        Long userId,
        String username
) {
}
