package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.EditTaskDto;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskShortDto;
import com.febfes.fftmback.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {FileMapper.class})
public interface TaskMapper {

    @Mapping(target = "id", source = "task.id")
    @Mapping(target = "type", source = "task.taskType.name")
    @Mapping(target = "files", source = "files")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "assignee", source = "assignee")
    TaskDto taskViewToTaskDto(TaskView task, List<FileEntity> files, UserDto owner, UserDto assignee);

    @Mapping(target = "id", source = "task.id")
    @Mapping(target = "type", source = "task.taskType.name")
    @Mapping(target = "order", source = "task.entityOrder")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "assignee", source = "assignee")
    TaskShortDto taskViewToTaskShortDto(TaskView task, UserDto owner, UserDto assignee);

    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "taskType.name", source = "taskDto.type")
    @Mapping(target = "entityOrder", source = "taskDto.order")
    TaskEntity taskDtoToTask(Long projectId, Long columnId, EditTaskDto taskDto);
}
