package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskShortDto;
import com.febfes.fftmback.dto.constant.TaskPriority;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "priority", qualifiedByName = "priorityToString")
    @Mapping(target = "type", source = "taskType.name")
    TaskDto taskToTaskDto(TaskEntity taskEntity);

    @Mapping(target = "priority", qualifiedByName = "priorityToString")
    @Mapping(target = "type", source = "taskType.name")
    TaskShortDto taskToTaskShortDto(TaskEntity task);

    @Mapping(target = "taskType", ignore = true)
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "columnId", source = "columnId")
    @Mapping(target = "priority", qualifiedByName = "stringToPriority")
    @Mapping(target = "taskType.name", source = "taskDto.type")
    TaskEntity taskDtoToTask(Long projectId, Long columnId, TaskDto taskDto);


    @Named("stringToPriority")
    static TaskPriority stringToPriority(String str) {
        return str == null ? null : TaskPriority.valueOf(str.toUpperCase());
    }

    @Named("priorityToString")
    static String priorityToString(TaskPriority priority) {
        return priority == null ? null : priority.name().toLowerCase();
    }
}
