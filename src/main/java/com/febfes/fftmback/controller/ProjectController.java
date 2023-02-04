package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project")
public class ProjectController {

    private final @NonNull ProjectService projectService;

    @Operation(summary = "Get all projects")
    @ApiGet
    public List<ProjectDto> getProjects() {
        return projectService.getProjects().stream()
                .map(ProjectMapper.INSTANCE::projectToProjectDto)
                .toList();
    }

    @Operation(summary = "Create new project")
    @ApiCreate
    public ProjectDto createNewProject(@RequestBody ProjectDto projectDto) {
        return ProjectMapper.INSTANCE.projectToProjectDto(projectService.createProject(projectDto));
    }

    @Operation(summary = "Get project by its id")
    @ApiGetOne(path = "/{id}")
    @SuppressWarnings("MVCPathVariableInspection") // fake warn "Cannot resolve path variable 'id' in @RequestMapping"
    public ProjectDto getProject(@PathVariable Long id) {
        return ProjectMapper.INSTANCE.projectToProjectDto(projectService.getProject(id));
    }

    @Operation(summary = "Edit project by its id")
    @ApiEdit(path = "/{id}")
    public void editProject(@PathVariable Long id,
                            @RequestBody ProjectDto projectDto
    ) {
        projectService.editProject(id, projectDto);
    }

    @Operation(summary = "Delete project by its id")
    @ApiDelete(path = "/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
