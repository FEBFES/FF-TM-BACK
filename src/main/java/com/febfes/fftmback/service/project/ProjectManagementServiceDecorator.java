package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskTypeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service("projectManagementServiceDecorator")
@Transactional
@RequiredArgsConstructor
public class ProjectManagementServiceDecorator implements ProjectManagementService {

    @Qualifier("projectManagementService")
    private final ProjectManagementService projectManagementService;

    private final ColumnService columnService;
    private final TaskTypeService taskTypeService;
    private final ProjectMemberService projectMemberService;

    @Override
    public ProjectEntity createProject(ProjectEntity project, Long userId) {
        var createdProject = projectManagementService.createProject(project, userId);
        Long projectId = createdProject.getId();
        CompletableFuture.runAsync(() -> columnService.createDefaultColumnsForProject(projectId));
        CompletableFuture.runAsync(() -> taskTypeService.createDefaultTaskTypesForProject(projectId));
        // by default, the owner will also be a member of the project
        CompletableFuture.runAsync(() -> projectMemberService.addUserToProjectAndChangeRole(projectId, userId, RoleName.OWNER));
        return createdProject;
    }

    @Override
    public ProjectEntity getProject(Long id) {
        return projectManagementService.getProject(id);
    }

    @Override
    public ProjectEntity editProject(Long id, ProjectEntity project) {
        return projectManagementService.editProject(id, project);
    }

    @Override
    public void editProjectPartially(Long id, Long ownerId, List<PatchDto> patchDtoList) {
        projectManagementService.editProjectPartially(id, ownerId, patchDtoList);
    }

    @Override
    public void deleteProject(Long id) {
        taskTypeService.deleteAllTypesByProjectId(id);
        projectManagementService.deleteProject(id);
    }
}
