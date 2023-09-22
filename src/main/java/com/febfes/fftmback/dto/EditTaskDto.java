package com.febfes.fftmback.dto;

import com.febfes.fftmback.domain.common.TaskPriority;
import jakarta.validation.constraints.NotBlank;

public record EditTaskDto(

        @NotBlank(message = "Invalid Name: Empty name")
        String name,

        String description,

        Long assigneeId,

        TaskPriority priority,

        String type,

        Integer order
) {
}
