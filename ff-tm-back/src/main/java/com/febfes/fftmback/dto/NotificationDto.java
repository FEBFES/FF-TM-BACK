package com.febfes.fftmback.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        LocalDateTime createDate,
        String message,
        boolean isRead
) {
}
