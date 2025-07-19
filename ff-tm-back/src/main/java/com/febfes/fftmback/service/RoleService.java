package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.RoleEntity;

import java.util.List;

public interface RoleService {

    List<RoleEntity> getRoles();

    RoleEntity getUserRoleOnProject(Long projectId, Long userId);

    void changeUserRoleOnProject(Long projectId, Long userId, RoleName roleName);
}
