package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.config.jwt.User;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.domain.projection.MemberProjection;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.ProjectForUserDto;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.service.project.ProjectManagementService;
import com.febfes.fftmback.service.project.ProjectMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Qualifier("projectManagementServiceDecorator")
    private final ProjectManagementService projectManagementService;
    private final ProjectMemberService projectMemberService;
    private final TaskTypeService taskTypeService;
    private final ProjectMapper projectMapper;
    private final UserService userService;


    @Operation(summary = "Get all projects for authenticated user")
    @ApiGet
    public List<ProjectDto> getProjectsForUser(
            @SortParam @RequestParam(defaultValue = "-createDate") String[] sort,
            @AuthenticationPrincipal User user
    ) {
        return projectMemberService.getProjectsForUser(user.id(), getOrderFromParams(sort));
    }

    @Operation(summary = "Create new project")
    @ApiCreate
    public ProjectDto createNewProject(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid ProjectDto projectDto
    ) {
        ProjectEntity project = projectManagementService.createProject(
                projectMapper.projectDtoToProject(projectDto), user.id()
        );
        return projectMapper.projectToProjectDto(project);
    }

    @Operation(summary = "Get project by its id")
    @ApiGetOne(path = "{id}")
    @SuppressWarnings("MVCPathVariableInspection") // fake warn "Cannot resolve path variable 'id' in @RequestMapping"
    public OneProjectDto getProject(@AuthenticationPrincipal User user, @PathVariable Long id) {
        ProjectForUserDto project = projectMemberService.getProjectForUser(id, user.id());
        List<MemberProjection> members = userService.getProjectMembersWithRole(id);
        return projectMapper.projectWithMembersToOneProjectDto(project, members);
    }

    @Operation(summary = "Edit project by its id")
    @ApiEdit(path = "{id}")
    @PreAuthorize("hasAuthority(T(com.febfes.fftmback.domain.common.RoleName).MEMBER_PLUS.name())")
    public ProjectDto editProject(
            @PathVariable Long id,
            @RequestBody ProjectDto projectDto
    ) {
        return projectMapper.projectToProjectDto(
                projectManagementService.editProject(id, projectMapper.projectDtoToProject(projectDto))
        );
    }

    @Operation(summary = "Delete project by its id")
    @ApiDelete(path = "{id}")
    @PreAuthorize("hasAuthority(T(com.febfes.fftmback.domain.common.RoleName).OWNER.name())")
    public void deleteProject(@PathVariable Long id) {
        projectManagementService.deleteProject(id);
    }

    @Operation(summary = "Edit project partially")
    @ApiPatch(path = "{id}")
    public void editProjectPartially(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody List<PatchDto> patchDtoList
    ) {
        projectManagementService.editProjectPartially(id, user.id(), patchDtoList);
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
