package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiGet;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/roles")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Role")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Get all roles")
    @ApiGet
    public List<RoleEntity> getRoles() {
        return roleService.getRoles();
    }

    @Operation(summary = "Change user role on a project")
    @PostMapping(path = "{roleName}/projects/{projectId}/users/{userId}/")
    @PreAuthorize("hasAuthority(T(com.febfes.fftmback.domain.common.RoleName).OWNER.name())")
    public void changeUserRoleOnProject(
            @PathVariable RoleName roleName,
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        roleService.changeUserRoleOnProject(projectId, userId, roleName);
    }
}
