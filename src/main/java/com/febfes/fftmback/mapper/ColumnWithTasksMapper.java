package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = TaskShortMapper.class)
public interface ColumnWithTasksMapper {

    ColumnWithTasksMapper INSTANCE = Mappers.getMapper(ColumnWithTasksMapper.class);

    @Mapping(target = "tasks", source = "taskEntityList")
    ColumnWithTasksDto columnToColumnWithTasksDto(TaskColumnEntity columnEntity);
}
