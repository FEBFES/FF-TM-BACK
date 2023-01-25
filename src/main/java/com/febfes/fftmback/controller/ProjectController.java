package com.febfes.fftmback.controller;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final @NonNull ProjectService projectService;

    @Operation(summary = "Get all projects")
    @GetMapping
    public List<ProjectDto> getProjects() {
        return projectService.getProjects().stream().map(ProjectService::mapToProjectDto).toList();
    }

    @Operation(summary = "Create new project")
    @PostMapping
    public @ResponseBody ProjectDto createNewProject(@RequestBody ProjectDto projectDto
    ) {
        return ProjectService.mapToProjectDto(projectService.createProject(projectDto));
    }

    @Operation(summary = "Get project by its id")
    @GetMapping(path = "{id}")
    public @ResponseBody ProjectDto getProject(@PathVariable Long id) {
        ProjectEntity projectEntity = projectService.getProject(id);
        if (ProjectService.isEmptyProject(projectEntity)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        return ProjectService.mapToProjectDto(projectEntity);
    }

    @Operation(summary = "Edit project by its id")
    @PutMapping(path = "{id}")
    public boolean editProject(@PathVariable Long id,
                               @RequestBody ProjectDto projectDto
    ) {
        return projectService.editProject(id, projectDto);
    }

    @Operation(summary = "Delete project by its id")
    @DeleteMapping(path = "{id}")
    public boolean deleteProject(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }
}
