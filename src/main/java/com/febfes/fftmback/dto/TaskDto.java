package com.febfes.fftmback.dto;

import com.febfes.fftmback.domain.common.TaskPriority;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record TaskDto(

        Long id,
        String name,
        String description,
        Date createDate,
        Long projectId,
        Long columnId,
        TaskPriority priority,
        String type,
        Long filesCounter,
        List<TaskFileDto> files,
        UserDto owner,
        UserDto assignee,

        Date updateDate
) {

}
