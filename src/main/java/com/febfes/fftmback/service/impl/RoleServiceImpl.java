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
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public List<RoleEntity> getRoles() {
        List<RoleEntity> roles = roleRepository.findAll();
        log.info("Received {} roles", roles);
        return roles;
    }

    @Override
    public RoleEntity getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(Exceptions.roleNotFound(roleName));
    }

    @Override
    public void changeUserRoleOnProject(Long projectId, Long userId, RoleName roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(Exceptions.userNotFoundById(userId));
        changeUserRoleOnProject(projectId, user, roleName);
    }

    @Override
    public void changeUserRoleOnProject(Long projectId, UserEntity user, RoleName roleName) {
        RoleEntity ownerRole = getRoleByName(roleName);
        user.getProjectRoles().put(projectId, ownerRole);
        userRepository.save(user);
        log.info("Changed user roleOnProject on project. User id: {}, Project id: {}, Role name: {}",
                user.getId(), projectId, roleName.name());
    }
}
