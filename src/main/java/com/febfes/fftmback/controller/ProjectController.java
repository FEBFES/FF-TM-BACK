package com.febfes.fftmback.controller;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.service.ProjectService;
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

    @GetMapping
    public List<ProjectDto> getProjects() {
        return projectService.getProjects().stream().map(ProjectService::mapToProjectDto).toList();
    }

    @PostMapping
    public @ResponseBody ProjectDto createNewProject(@RequestBody ProjectDto projectDto
    ) {
        return ProjectService.mapToProjectDto(projectService.createProject(projectDto));
    }

    @GetMapping(path = "{id}")
    public @ResponseBody ProjectDto getProject(@PathVariable Long id) {
        ProjectEntity projectEntity = projectService.getProject(id);
        if (ProjectService.isEmptyProject(projectEntity)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }
        return ProjectService.mapToProjectDto(projectEntity);
    }

    @PutMapping(path = "{id}")
    public boolean editProject(@PathVariable Long id,
                               @RequestBody ProjectDto projectDto
    ) {
        return projectService.editProject(id, projectDto);
    }

    @DeleteMapping(path = "{id}")
    public boolean deleteProject(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }
}
