package com.febfes.fftmback.dto;

import java.util.Date;

public record TaskShortDto(
        Long id,
        String name,
        String description,
        Long columnId,
        Long projectId,
        Long ownerId,
        Date createDate

) {
}
