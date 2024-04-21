package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.dto.ProjectDto;

import java.util.List;

public interface ProjectManagementService {

    ProjectEntity createProject(ProjectEntity project, Long userId);

    ProjectEntity getProject(Long id);

    ProjectDto editProject(Long id, ProjectEntity project);

    void editProjectPartially(Long id, Long ownerId, List<PatchDto> patchDtoList);

    void deleteProject(Long id);
}
