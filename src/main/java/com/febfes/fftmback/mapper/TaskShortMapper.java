package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.TaskShortDto;
import org.mapstruct.Mapper;

@Mapper
public interface TaskShortMapper {

    TaskShortDto taskToTaskShortDto(TaskEntity task);
}
