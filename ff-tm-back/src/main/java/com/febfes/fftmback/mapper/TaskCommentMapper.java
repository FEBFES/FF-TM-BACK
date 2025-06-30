package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;
import com.febfes.fftmback.domain.projection.TaskCommentProjection;
import com.febfes.fftmback.dto.TaskCommentDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskCommentMapper {

    TaskCommentEntity taskCommentToTaskCommentEntity(TaskCommentDto taskCommentDto);

    TaskCommentDto projectionToDto(TaskCommentProjection projection);

    List<TaskCommentDto> projectionListToDtoList(List<TaskCommentProjection> projection);
}
