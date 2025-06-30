package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ColumnMapper {

    @Mapping(target = "order", ignore = true)
    ColumnDto columnToColumnDto(TaskColumnEntity columnEntity);

    @Mapping(target = "projectId", source = "projectId")
    TaskColumnEntity columnDtoToColumn(ColumnDto columnDto, Long projectId);

}
