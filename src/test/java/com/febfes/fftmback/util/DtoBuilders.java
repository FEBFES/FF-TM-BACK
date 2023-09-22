package com.febfes.fftmback.util;

import com.febfes.fftmback.domain.common.TaskPriority;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.TaskDto;
import lombok.experimental.UtilityClass;
import org.springframework.context.annotation.Profile;

@UtilityClass
@Profile("test")
public class DtoBuilders {

    public ProjectDto createProjectDto(String name) {
        return ProjectDto.builder()
                .name(name)
                .build();
    }

    public ProjectDto createProjectDto(String name, String description) {
        return ProjectDto.builder()
                .name(name)
                .description(description)
                .build();
    }

    public ColumnDto createColumnDto(String name) {
        return ColumnDto.builder()
                .name(name)
                .build();
    }

    public ColumnDto createColumnDto(String name, Integer order) {
        return ColumnDto.builder()
                .name(name)
                .order(order)
                .build();
    }

    public TaskDto createTaskDto(String name) {
        return TaskDto.builder()
                .name(name)
                .filesCounter(0L)
                .build();
    }

    public TaskDto createTaskDtoWithType(String name, String type) {
        return TaskDto.builder()
                .name(name)
                .type(type)
                .filesCounter(0L)
                .build();
    }

    public TaskDto createTaskDtoWithPriority(String name, String priority) {
        return TaskDto.builder()
                .name(name)
                .filesCounter(0L)
                .priority(TaskPriority.valueOf(priority.toUpperCase()))
                .build();
    }

    public TaskEntity createTaskEntity(Long projectId, Long columnId, String taskName) {
        return TaskEntity.builder().projectId(projectId).columnId(columnId).name(taskName).build();
    }
}
