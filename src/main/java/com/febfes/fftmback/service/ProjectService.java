package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;

import java.util.List;

public interface ProjectService {
    ProjectEntity createProject(ProjectEntity project, String username);

    List<ProjectEntity> getProjects();

    ProjectEntity getProject(Long id);

    void editProject(Long id, ProjectEntity project);

    void deleteProject(Long id);

    DashboardDto getDashboard(Long id, String taskFilter);
}
