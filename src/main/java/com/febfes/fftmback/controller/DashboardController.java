package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiGetOne;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project")
public class DashboardController {

    private final @NonNull ProjectService projectService;

    @Operation(summary = "Get dashboard by project id")
    @ApiGetOne(path = "{id}/dashboard")
    @SuppressWarnings("MVCPathVariableInspection") // fake warn "Cannot resolve path variable 'id' in @RequestMapping"
    public DashboardDto getDashboard(@PathVariable Long id) {
        return projectService.getDashboard(id);
    }
}
