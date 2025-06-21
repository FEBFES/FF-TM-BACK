package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.repository.RoleRepository;
import com.febfes.fftmback.repository.UserRepository;
import com.febfes.fftmback.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Cacheable("roles")
    public List<RoleEntity> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Cacheable(value = "roles", key = "#roleName")
    public RoleEntity getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(Exceptions.roleNotFound(roleName));
    }

    @Override
    @CacheEvict(value = "roles", allEntries = true)
    public void changeUserRoleOnProject(Long projectId, Long userId, RoleName roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(Exceptions.userNotFoundById(userId));
        changeUserRoleOnProject(projectId, user, roleName);
    }

    @Override
    @CacheEvict(value = "roles", allEntries = true)
    public void changeUserRoleOnProject(Long projectId, UserEntity user, RoleName roleName) {
        RoleEntity ownerRole = getRoleByName(roleName);
        user.getProjectRoles().put(projectId, ownerRole);
        userRepository.save(user);
        log.info("The user's role on the project has been changed. User id: {}, Project id: {}, Role name: {}",
                user.getId(), projectId, roleName.name());
    }
}
