package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.repository.RoleRepository;
import com.febfes.fftmback.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Cacheable("roles")
    public List<RoleEntity> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public RoleEntity getUserRoleOnProject(Long projectId, Long userId) {
        return roleRepository.getUserRoleOnProject(projectId, userId)
                .orElseThrow(Exceptions.roleNotFoundByProjectId(projectId));
    }

    @Override
    @Transactional
    public void changeUserRoleOnProject(Long projectId, Long userId, RoleName roleName) {
        RoleEntity ownerRole = roleRepository.findByName(roleName)
                .orElseThrow(Exceptions.roleNotFound(roleName));
        int res = roleRepository.changeUserRoleOnProject(projectId, userId, ownerRole.getName().name());
        if (res == 0) {
            // TODO: custom exception???
            throw new RuntimeException("Role change failed");
        }
        log.info("The user's role on the project has been changed. User id: {}, Project id: {}, Role name: {}",
                userId, projectId, roleName.name());
    }
}
