package com.febfes.fftmback.util;

import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.TaskDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class DtoBuilders {
    public ProjectDto createProjectDto(String name) {
        return new ProjectDto(null, name, null, null, null);
    }

    public ProjectDto createProjectDto(String name, String description) {
        return new ProjectDto(null, name, description, null, null);
    }

    public ColumnDto createColumnDto() {
        return new ColumnDto(null, null, null, null, null, null);
    }

    public ColumnDto createColumnDto(String name) {
        return new ColumnDto(null, name, null, null, null, null);
    }

    public ColumnDto createColumnDto(String name, Long childTaskColumnId) {
        return new ColumnDto(null, name, null, null, null, childTaskColumnId);
    }

    public TaskDto createTaskDto() {
        return new TaskDto(null, null, null, null, null, null, null);
    }

    public TaskDto createTaskDto(String name) {
        return new TaskDto(null, name, null, null, null, null, null);
    }
}
