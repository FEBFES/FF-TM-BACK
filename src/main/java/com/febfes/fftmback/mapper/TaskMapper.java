package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.EditTaskDto;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskShortDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = FileMapper.class)
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "type", source = "task.taskType.name")
    @Mapping(target = "files", source = "files")
    TaskDto taskViewToTaskDto(TaskView task, List<FileEntity> files);

    @Mapping(target = "type", source = "task.taskType.name")
    TaskShortDto taskViewToTaskShortDto(TaskView task);

    @Mapping(target = "taskType.name", source = "taskDto.type")
    TaskEntity taskDtoToTask(Long projectId, Long columnId, EditTaskDto taskDto);
}
