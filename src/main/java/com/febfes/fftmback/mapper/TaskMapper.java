package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.UserPicEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskFileDto;
import com.febfes.fftmback.dto.TaskShortDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static java.util.Objects.isNull;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "type", source = "task.taskType.name")
    TaskDto taskToTaskDto(TaskEntity task);

    @Mapping(target = "type", source = "task.taskType.name")
    @Mapping(target = "files", source = "files")
    TaskDto taskToTaskDto(TaskEntity task, List<TaskFileDto> files);

    @Mapping(target = "ownerUserPic", qualifiedByName = "userPicToString")
    @Mapping(target = "type", source = "task.taskType.name")
    TaskShortDto taskToTaskShortDto(TaskEntity task);

    @Mapping(target = "taskType", ignore = true)
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "columnId", source = "columnId")
    @Mapping(target = "taskType.name", source = "taskDto.type")
    TaskEntity taskDtoToTask(Long projectId, Long columnId, TaskDto taskDto);

    @Named("userPicToString")
    static String userPicToString(UserPicEntity userPic) {
        return isNull(userPic) ? null : userPic.getFileUrn();
    }
}
