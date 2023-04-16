package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Project")
public class ProjectController {

    private final @NonNull ProjectService projectService;
    private final @NonNull TaskTypeService taskTypeService;

    @Operation(summary = "Get all projects for authenticated user")
    @ApiGet
    public List<ProjectDto> getProjectsForUser() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        return projectService.getProjectsByOwnerId(userId).stream()
                .map(ProjectMapper.INSTANCE::projectToProjectDto)
                .toList();
    }

    @Operation(summary = "Create new project")
    @ApiCreate
    public ProjectDto createNewProject(@RequestBody @Valid ProjectDto projectDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ProjectEntity project = projectService.createProject(
                ProjectMapper.INSTANCE.projectDtoToProject(projectDto), authentication.getName()
        );
        return ProjectMapper.INSTANCE.projectToProjectDto(project);
    }

    @Operation(summary = "Get project by its id")
    @ApiGetOne(path = "{id}")
    @SuppressWarnings("MVCPathVariableInspection") // fake warn "Cannot resolve path variable 'id' in @RequestMapping"
    public ProjectDto getProject(@PathVariable Long id) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ProjectMapper.INSTANCE.projectToProjectDto(projectService.getProjectByOwnerId(id, user.getId()));
    }

    @Operation(summary = "Edit project by its id")
    @ApiEdit(path = "{id}")
    public void editProject(
            @PathVariable Long id,
            @RequestBody ProjectDto projectDto
    ) {
        projectService.editProject(id, ProjectMapper.INSTANCE.projectDtoToProject(projectDto));
    }

    @Operation(summary = "Delete project by its id")
    @ApiDelete(path = "{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @Operation(summary = "Edit project partially")
    @ApiPatch(path = "{id}")
    public void editProjectPartially(
            @PathVariable Long id,
            @RequestBody List<PatchDto> patchDtoList
    ) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectService.editProjectPartially(id, user.getId(), patchDtoList);
    }

    @Operation(summary = "Get task types for project")
    @ApiGet(path = "{id}/task-types")
    public List<String> getTaskTypes(@PathVariable Long id) {
        return taskTypeService
                .getTaskTypesByProjectId(id)
                .stream()
                .map(TaskTypeEntity::getName)
                .collect(Collectors.toList());
    }
}
