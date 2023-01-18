package com.example.fftmback.controller;

import com.example.fftmback.domain.ProjectEntity;

import com.example.fftmback.service.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static ch.qos.logback.core.util.AggregationType.NOT_FOUND;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final @NonNull ProjectService projectService;

    @GetMapping
    public List<ProjectEntity> getProjects() {
        return projectService.getProjects();
    }

    @PostMapping
    public @ResponseBody ProjectEntity createNewProject(@RequestParam String name,
                                                        @RequestParam String description
    ) {
        return projectService.createProject(name, description);
    }

    @GetMapping(path = "{id}")
    public @ResponseBody ProjectEntity getProject(@PathVariable Long id) {
        ProjectEntity projectEntity = projectService.getProject(id);

        if (projectService.isEmptyProject(projectEntity)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        return projectEntity;
    }
    @PutMapping(path = "{id}/editProject")
    public boolean editProject(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String description
    ) {
        return projectService.editProject(id, name, description);
    }

    @DeleteMapping(path = "{id}")
    public boolean deleteProject(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }
}
