package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = TaskMapper.class)
public interface ColumnWithTasksMapper {

    @Mapping(target = "order", source = "columnEntity.entityOrder")
    @Mapping(target = "tasks", source = "taskList")
    ColumnWithTasksDto columnToColumnWithTasksDto(TaskColumnEntity columnEntity, List<TaskView> taskList);
}
