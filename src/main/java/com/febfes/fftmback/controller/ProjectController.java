package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final @NonNull ProjectService projectService;

    @Operation(summary = "Get all projects")
    @ApiGet
    public List<ProjectDto> getProjects() {
        return projectService.getProjects().stream().map(ProjectService::mapToProjectDto).toList();
    }

    @Operation(summary = "Create new project")
    @ApiCreate
    public ProjectDto createNewProject(@RequestBody ProjectDto projectDto) {
        return ProjectService.mapToProjectDto(projectService.createProject(projectDto));
    }

    @Operation(summary = "Get project by its id")
    @ApiGetOne(path = "/{id}")
    @SuppressWarnings("MVCPathVariableInspection") // fake warn "Cannot resolve path variable 'id' in @RequestMapping"
    public ProjectDto getProject(@PathVariable Long id) {
        Optional<ProjectEntity> project = projectService.getProject(id);
        if (project.isEmpty()) {
            throw new EntityNotFoundException(ProjectEntity.class.toString(), id);
        }
        return ProjectService.mapToProjectDto(project.get());
    }

    @Operation(summary = "Edit project by its id")
    @ApiEdit(path = "/{id}")
    public boolean editProject(@PathVariable Long id,
                               @RequestBody ProjectDto projectDto
    ) {
        // TODO: сделать этот метод также, как и в TaskController
        return projectService.editProject(id, projectDto);
    }

    @Operation(summary = "Delete project by its id")
    @ApiDelete(path = "/{id}")
    public boolean deleteProject(@PathVariable Long id) {
        // TODO: сделать этот метод также, как и в TaskController (можно оставить возврат boolean, но по идее статуса ошибки будет достаточно)
        return projectService.deleteProject(id);
    }
}
