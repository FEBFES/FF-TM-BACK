package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.UserView;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.PatchDto;

import java.util.List;

public interface ProjectService {
    ProjectEntity createProject(ProjectEntity project, String username);

    List<ProjectEntity> getProjectsForUser(Long userId);

    ProjectEntity getProject(Long id);

    OneProjectDto getProjectForUser(Long id, Long userId);

    void editProject(Long id, ProjectEntity project);

    void deleteProject(Long id);

    DashboardDto getDashboard(Long id, String taskFilter);

    void editProjectPartially(Long id, Long ownerId, List<PatchDto> patchDtoList);

    void addProjectToFavourite(Long projectId, Long userId);

    void removeProjectFromFavourite(Long projectId, Long userId);

    List<UserView> getProjectMembers(Long projectId);

    List<UserView> addNewMembers(Long projectId, List<Long> memberIds);

    UserView removeMember(Long projectId, Long memberId);
}
