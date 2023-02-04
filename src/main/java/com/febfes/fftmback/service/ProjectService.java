package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.ProjectDto;

import java.util.List;

public interface ProjectService {
    ProjectEntity createProject(ProjectDto projectDto);

    List<ProjectEntity> getProjects();

    ProjectEntity getProject(Long id);

    void editProject(Long id, ProjectDto projectDto);

    void deleteProject(Long id);

    DashboardDto getDashboard(Long id);
}
