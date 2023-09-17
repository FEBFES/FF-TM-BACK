package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.*;

import java.util.List;

public interface ProjectService {

    ProjectEntity createProject(ProjectEntity project, Long userId);

    List<ProjectDto> getProjectsForUser(Long userId);

    ProjectEntity getProject(Long id);

    OneProjectDto getProjectForUser(Long id, Long userId);

    ProjectDto editProject(Long id, ProjectEntity project);

    void deleteProject(Long id);

    DashboardDto getDashboard(Long id, TaskSpec taskSpec);

    void editProjectPartially(Long id, Long ownerId, List<PatchDto> patchDtoList);

    void addProjectToFavourite(Long projectId, Long userId);

    void removeProjectFromFavourite(Long projectId, Long userId);

    List<MemberDto> addNewMembers(Long projectId, List<Long> memberIds);

    MemberDto removeMember(Long projectId, Long memberId);
}
