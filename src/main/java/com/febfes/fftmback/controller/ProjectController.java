package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.config.auth.RoleCheckerComponent;
import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.TaskTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.febfes.fftmback.util.SortUtils.getOrderFromParams;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Project")
public class ProjectController {

    private final @NonNull ProjectService projectService;
    private final @NonNull TaskTypeService taskTypeService;
    private final @NonNull RoleCheckerComponent roleCheckerComponent;

    @Operation(summary = "Get all projects for authenticated user")
    @ApiGet
    public List<ProjectDto> getProjectsForUser(
            @SortParam @RequestParam(defaultValue = "-createDate") String[] sort,
            @AuthenticationPrincipal UserEntity user
    ) {
        return projectService.getProjectsForUser(user.getId(), getOrderFromParams(sort));
    }

    @Operation(summary = "Create new project")
    @ApiCreate
    public ProjectDto createNewProject(
            @AuthenticationPrincipal UserEntity user,
            @RequestBody @Valid ProjectDto projectDto
    ) {
        ProjectEntity project = projectService.createProject(
                ProjectMapper.INSTANCE.projectDtoToProject(projectDto), user.getId()
        );
        return ProjectMapper.INSTANCE.projectToProjectDto(project);
    }

    @Operation(summary = "Get project by its id")
    @ApiGetOne(path = "{id}")
    @SuppressWarnings("MVCPathVariableInspection") // fake warn "Cannot resolve path variable 'id' in @RequestMapping"
    public OneProjectDto getProject(@AuthenticationPrincipal UserEntity user, @PathVariable Long id) {
        return projectService.getProjectForUser(id, user.getId());
    }

    @Operation(summary = "Edit project by its id")
    @ApiEdit(path = "{id}")
    public ProjectDto editProject(
            @PathVariable Long id,
            @RequestBody ProjectDto projectDto
    ) {
        roleCheckerComponent.checkIfHasRole(id, RoleName.MEMBER_PLUS);
        return projectService.editProject(id, ProjectMapper.INSTANCE.projectDtoToProject(projectDto));
    }

    @Operation(summary = "Delete project by its id")
    @ApiDelete(path = "{id}")
    public void deleteProject(@PathVariable Long id) {
        roleCheckerComponent.checkIfHasRole(id, RoleName.OWNER);
        projectService.deleteProject(id);
    }

    @Operation(summary = "Edit project partially")
    @ApiPatch(path = "{id}")
    public void editProjectPartially(
            @AuthenticationPrincipal UserEntity user,
            @PathVariable Long id,
            @RequestBody List<PatchDto> patchDtoList
    ) {
        projectService.editProjectPartially(id, user.getId(), patchDtoList);
    }

    @Operation(summary = "Get task types for project")
    @ApiGet(path = "{id}/task-types")
    public List<String> getTaskTypes(@PathVariable Long id) {
        return taskTypeService
                .getTaskTypesByProjectId(id)
                .stream()
                .map(TaskTypeEntity::getName)
                .toList();
    }
}
