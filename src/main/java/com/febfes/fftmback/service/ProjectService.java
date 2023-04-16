package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.PatchDto;

import java.util.List;

public interface ProjectService {
    ProjectEntity createProject(ProjectEntity project, String username);

    List<ProjectEntity> getProjectsByOwnerId(Long ownerId);

    ProjectEntity getProject(Long id);

    ProjectEntity getProjectByOwnerId(Long id, Long ownerId);

    void editProject(Long id, ProjectEntity project);

    void deleteProject(Long id);

    DashboardDto getDashboard(Long id, String taskFilter);

    void editProjectPartially(Long id, Long ownerId, List<PatchDto> patchDtoList);

    void addProjectToFavourite(Long projectId, Long userId);

    void removeProjectFromFavourite(Long projectId, Long userId);
}
