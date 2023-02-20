package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ColumnMapper {

    ColumnMapper INSTANCE = Mappers.getMapper(ColumnMapper.class);

    ColumnDto columnToColumnDto(TaskColumnEntity columnEntity);

    @Mapping(target = "projectId", source = "projectId")
    TaskColumnEntity columnDtoToColumn(ColumnDto columnDto, Long projectId);

    @Mapping(target = "id", source = "columnId")
    @Mapping(target = "projectId", source = "projectId")
    TaskColumnEntity columnDtoToColumn(ColumnDto columnDto, Long columnId, Long projectId);

}
