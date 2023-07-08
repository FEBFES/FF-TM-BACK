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

    @Mapping(target = "tasks", source = "filteredTasks")
    ColumnWithTasksDto columnToColumnWithTasksDto(TaskColumnEntity columnEntity, List<TaskView> filteredTasks);
}
