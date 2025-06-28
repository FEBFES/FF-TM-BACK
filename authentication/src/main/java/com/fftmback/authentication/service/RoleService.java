package com.fftmback.authentication.service;

import com.febfes.fftmback.domain.RoleName;
import com.fftmback.authentication.domain.RoleEntity;
import com.fftmback.authentication.domain.UserEntity;

import java.util.List;

public interface RoleService {

    List<RoleEntity> getRoles();

    void changeUserRoleOnProject(Long projectId, Long userId, RoleName roleName);

    void changeUserRoleOnProject(Long projectId, UserEntity user, RoleName roleName);
}
