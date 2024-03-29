package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = TaskMapper.class)
public interface ColumnWithTasksMapper {

    ColumnWithTasksMapper INSTANCE = Mappers.getMapper(ColumnWithTasksMapper.class);

    @Mapping(target = "order", source = "columnEntity.entityOrder")
    @Mapping(target = "tasks", source = "taskList")
    ColumnWithTasksDto columnToColumnWithTasksDto(TaskColumnEntity columnEntity, List<TaskView> taskList);
}
