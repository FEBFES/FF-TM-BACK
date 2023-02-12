package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Date;

@Mapper
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectDto projectToProjectDto(ProjectEntity project);

    @Mapping(target = "createDate", source = "createDate")
    ProjectEntity projectDtoToProject(ProjectDto projectDto, Date createDate);

    @Mapping(target = "createDate", source = "createDate")
    @Mapping(target = "ownerId", source = "ownerId")
    ProjectEntity projectDtoToProject(ProjectDto projectDto, Date createDate, Long ownerId);
}
