package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper
public interface ColumnMapper {

    ColumnMapper INSTANCE = Mappers.getMapper(ColumnMapper.class);

    ColumnDto columnToColumnDto(TaskColumnEntity columnEntity);

    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "createDate", source = "createDate")
    TaskColumnEntity columnDtoToColumn(ColumnDto columnDto, Long projectId, Date createDate);
}
