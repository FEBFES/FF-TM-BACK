package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ColumnMapper {

    ColumnMapper INSTANCE = Mappers.getMapper(ColumnMapper.class);

    ColumnDto columnToColumnDto(TaskColumnEntity columnEntity);
}
