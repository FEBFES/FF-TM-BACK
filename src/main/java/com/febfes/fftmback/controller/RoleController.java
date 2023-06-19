package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiGet;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
}
