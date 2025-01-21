package com.fftmback.dto;

public record SendNotificationDto(
        String message,
        Long userId
) {
}
