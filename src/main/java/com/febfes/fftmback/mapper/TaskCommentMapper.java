package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;
import com.febfes.fftmback.dto.TaskCommentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskCommentMapper {

    @Mapping(target = "creator.id", source = "creatorId")
    TaskCommentEntity taskCommentToTaskCommentEntity(TaskCommentDto taskCommentDto);

    @Mapping(target ="creatorId", source = "creator.id")
    @Mapping(target = "creatorName", source = ".", qualifiedByName = "getCreatorName")
    TaskCommentDto taskCommentEntityToTaskCommentDto(TaskCommentEntity taskCommentEntity);

    @Mapping(target ="creatorId", source = "creator.id")
    @Mapping(target = "creatorName", source = ".", qualifiedByName = "getCreatorName")
    List<TaskCommentDto> taskCommentEntityToTaskCommentDto(List<TaskCommentEntity> taskCommentEntityList);

    @Named("getCreatorName")
    default String getCreatorName(TaskCommentEntity taskCommentEntity) {
        return "%s %s".formatted(
                taskCommentEntity.getCreator().getFirstName(),
                taskCommentEntity.getCreator().getLastName()
        );
    }
}
