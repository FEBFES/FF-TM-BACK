package com.febfes.fftmback.dto;

public record ProjectSettingsDto(
        Long projectId,
        Long userId,
        Boolean isFavourite
) {
}
