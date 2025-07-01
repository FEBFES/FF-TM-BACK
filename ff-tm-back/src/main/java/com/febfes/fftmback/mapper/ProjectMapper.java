package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.projection.MemberProjection;
import com.febfes.fftmback.domain.projection.ProjectProjection;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.ProjectForUserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "isFavourite", ignore = true)
    ProjectDto projectToProjectDto(ProjectEntity project);

    ProjectEntity projectDtoToProject(ProjectDto projectDto);

    List<ProjectDto> projectProjectionToProjectDto(List<ProjectProjection> projectForUserDto);

    @Mapping(target = "members", source = "members")
    @Mapping(target = "userRoleOnProject.name", source = "projectForUserDto.roleName")
    @Mapping(target = "userRoleOnProject.description", source = "projectForUserDto.roleDescription")
    OneProjectDto projectWithMembersToOneProjectDto(
            ProjectForUserDto projectForUserDto,
            List<MemberProjection> members
    );
}
