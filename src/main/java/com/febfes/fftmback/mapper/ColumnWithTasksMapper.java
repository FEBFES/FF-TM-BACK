package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = TaskShortMapper.class)
public interface ColumnWithTasksMapper {

    @Mapping(target = "tasks", source = "taskEntityList")
    ColumnWithTasksDto columnToColumnWithTasksDto(TaskColumnEntity columnEntity);
}
