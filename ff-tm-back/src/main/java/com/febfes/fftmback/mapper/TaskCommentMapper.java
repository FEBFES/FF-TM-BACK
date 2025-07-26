package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;
import com.febfes.fftmback.dto.TaskCommentDto;
import com.febfes.fftmback.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskCommentMapper {

    TaskCommentEntity taskCommentToTaskCommentEntity(TaskCommentDto taskCommentDto);

    @Mapping(source = "entity.id", target = "id")
    @Mapping(source = "user.id", target = "creatorId")
    @Mapping(source = "user.username", target = "creatorName")
    @Mapping(source = "entity.taskId", target = "taskId")
    @Mapping(source = "entity.text", target = "text")
    TaskCommentDto mapToDto(TaskCommentEntity entity, UserDto user);
}
