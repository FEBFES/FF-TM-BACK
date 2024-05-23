package com.febfes.fftmback.dto;

import com.febfes.fftmback.domain.common.TaskPriority;

import java.time.LocalDateTime;

public record TaskShortDto(
        Long id,
        String name,
        String description,
        Long columnId,
        Long projectId,
        LocalDateTime createDate,
        TaskPriority priority,
        String type,
        Long filesCounter,
        UserDto owner,
        UserDto assignee,
        LocalDateTime updateDate,
        Integer order,
        LocalDateTime deadlineDate
) {
}
