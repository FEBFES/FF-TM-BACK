package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.projection.ProjectProjection;
import com.febfes.fftmback.domain.projection.ProjectWithMembersProjection;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {RoleMapper.class, UserMapper.class})
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectDto projectToProjectDto(ProjectEntity project);

    ProjectEntity projectDtoToProject(ProjectDto projectDto);

    ProjectDto projectProjectionToProjectDto(ProjectProjection projectProjection);

    @Mapping(target = "members", source = "members")
    @Mapping(target = "userRoleOnProject.name", source = "projectWithMembersProjection.roleName")
    @Mapping(target = "userRoleOnProject.description", source = "projectWithMembersProjection.roleDescription")
    OneProjectDto projectWithMembersProjectionToOneProjectDto(
            ProjectWithMembersProjection projectWithMembersProjection,
            List<MemberDto> members
    );
}
