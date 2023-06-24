package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.domain.dao.UserEntity;

import java.util.List;

public interface RoleService {

    List<RoleEntity> getRoles();

    RoleEntity getRoleByName(RoleName roleName);

    void changeUserRoleOnProject(Long projectId, Long userId, RoleName roleName);

    void changeUserRoleOnProject(ProjectEntity project, UserEntity user, RoleName roleName);
}
