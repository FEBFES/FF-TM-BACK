package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskFileEntity;
import com.febfes.fftmback.dto.TaskFileDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskFileMapper {

    TaskFileMapper INSTANCE = Mappers.getMapper(TaskFileMapper.class);

    TaskFileDto taskFileToDto(TaskFileEntity taskFileEntity);
}
