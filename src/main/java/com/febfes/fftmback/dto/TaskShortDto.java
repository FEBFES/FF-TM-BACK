package com.febfes.fftmback.dto;

import com.febfes.fftmback.domain.common.TaskPriority;

import java.util.Date;

public record TaskShortDto(
        Long id,
        String name,
        String description,
        Long columnId,
        Long projectId,
        Long ownerId,
        Date createDate,
        TaskPriority priority,
        String type,
        String ownerUserPic
) {
}
