package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.config.auth.RoleCheckerComponent;
import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.exception.ProjectOwnerException;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Project")
public class ProjectController {

    private final @NonNull ProjectService projectService;
    private final @NonNull TaskTypeService taskTypeService;
    private final @NonNull UserService userService;
    private final @NonNull RoleCheckerComponent roleCheckerComponent;

    @Operation(summary = "Get all projects for authenticated user")
    @ApiGet
    public List<ProjectDto> getProjectsForUser() {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = user.getId();
        return projectService.getProjectsForUser(userId);
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
    public OneProjectDto getProject(@PathVariable Long id) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
                .toList();
    }

    @Operation(summary = "Get project members")
    @ApiGet(path = "{id}/members")
    public List<MemberDto> getProjectMembers(@PathVariable Long id) {
        return userService.getProjectMembersWithRole(id);
    }

    @Operation(summary = "Add new members to the project")
    @PostMapping(path = "{id}/members")
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Only owner can add a new member", content = @Content)
    public List<MemberDto> addNewMembers(@PathVariable Long id, @RequestBody List<Long> memberIds) {
        roleCheckerComponent.checkIfHasRole(id, RoleName.MEMBER_PLUS);
        return projectService.addNewMembers(id, memberIds);
    }

    @Operation(summary = "Delete member from project")
    @DeleteMapping(path = "{id}/members/{memberId}")
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content)
    @ApiResponse(responseCode = "409", description = "Only owner can remove a member", content = @Content)
    public MemberDto removeMember(@PathVariable Long id, @PathVariable Long memberId) {
        roleCheckerComponent.checkIfHasRole(id, RoleName.MEMBER_PLUS);
        if (roleCheckerComponent.userHasRole(id, memberId, RoleName.OWNER)) {
            throw new ProjectOwnerException();
        }
        return projectService.removeMember(id, memberId);
    }
}
