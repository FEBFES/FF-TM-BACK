package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

// TODO: delete? it's isn't used anywhere
@Mapper(uses = ColumnWithTasksMapper.class)
public interface DashboardMapper {

    DashboardMapper INSTANCE = Mappers.getMapper(DashboardMapper.class);

    @Mapping(target = "columns", source = "taskColumnEntityList")
    DashboardDto projectToDashboardDto(ProjectEntity projectEntity);

}
