package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.projection.MemberProjection;
import com.febfes.fftmback.domain.projection.ProjectForUserProjection;
import com.febfes.fftmback.domain.projection.ProjectProjection;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ProjectMapper {

    ProjectDto projectToProjectDto(ProjectEntity project);

    ProjectEntity projectDtoToProject(ProjectDto projectDto);

    ProjectDto projectProjectionToProjectDto(ProjectProjection projectProjection);
    List<ProjectDto> projectProjectionToProjectDto(List<ProjectProjection> projectProjection);

    @Mapping(target = "members", source = "members")
    @Mapping(target = "userRoleOnProject.name", source = "projectForUserProjection.roleName")
    @Mapping(target = "userRoleOnProject.description", source = "projectForUserProjection.roleDescription")
    OneProjectDto projectWithMembersProjectionToOneProjectDto(
            ProjectForUserProjection projectForUserProjection,
            List<MemberProjection> members
    );
}
