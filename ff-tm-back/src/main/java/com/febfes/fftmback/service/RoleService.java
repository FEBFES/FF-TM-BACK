package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.domain.dao.UserEntity;

import java.util.List;

public interface RoleService {

    List<RoleEntity> getRoles();

    void changeUserRoleOnProject(Long projectId, Long userId, RoleName roleName);

    void changeUserRoleOnProject(Long projectId, UserEntity user, RoleName roleName);
}
