package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.specification.ColumnWithTasksSpec;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.PatchDto;

import java.util.List;
import java.util.Set;

public interface ProjectService {
    ProjectEntity createProject(ProjectEntity project, String username);

    List<ProjectEntity> getProjectsForUser(Long userId);

    ProjectEntity getProject(Long id);

    OneProjectDto getProjectForUser(Long id, Long userId);

    void editProject(Long id, ProjectEntity project);

    void deleteProject(Long id);

    DashboardDto getDashboard(Long id, ColumnWithTasksSpec columnWithTasksSpec);

    void editProjectPartially(Long id, Long ownerId, List<PatchDto> patchDtoList);

    void addProjectToFavourite(Long projectId, Long userId);

    void removeProjectFromFavourite(Long projectId, Long userId);

    Set<UserEntity> getProjectMembers(Long projectId);

    List<UserEntity> addNewMembers(Long projectId, List<Long> memberIds);

    UserEntity removeMember(Long projectId, Long memberId);
}
