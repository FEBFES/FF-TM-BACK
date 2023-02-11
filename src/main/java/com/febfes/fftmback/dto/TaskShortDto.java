package com.febfes.fftmback.dto;

public record TaskShortDto(
        Long id,
        String name,
        String description,
        Long columnId,
        Long projectId,
        Long ownerId
) {
}
