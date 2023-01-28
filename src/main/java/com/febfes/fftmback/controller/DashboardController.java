package com.febfes.fftmback.controller;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class DashboardController {

    private final @NonNull ProjectService projectService;

    @Operation(summary = "Get dashboard by project id")
    @GetMapping(path = "{id}")
    public @ResponseBody DashboardDto getProject(@PathVariable Long id) {
        Optional<ProjectEntity> project = projectService.getProject(id);
        if (project.isEmpty()) {
            throw new EntityNotFoundException(ProjectEntity.class.toString(), id);
        }
        return ProjectService.mapToDashboard(project.get());

    }
}
