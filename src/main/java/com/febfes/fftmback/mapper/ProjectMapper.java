package com.febfes.fftmback.mapper;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {RoleMapper.class, UserMapper.class})
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    ProjectDto projectToProjectDto(ProjectEntity project);

    ProjectEntity projectDtoToProject(ProjectDto projectDto);

//    @Mapping(target = "userRoleOnProject.name", source = "role.name")
//    @Mapping(target = "userRoleOnProject.description", source = "role.description")
//    @Mapping(target = "name", source = "project.name")
//    @Mapping(target = "description", source = "project.description")
//    @Mapping(target = "members[].userPic", qualifiedByName = "userPicToString")
//    OneProjectDto projectToOneProjectDto(ProjectEntity project, RoleDto role);
}
