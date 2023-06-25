package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;

    @Override
    public List<RoleEntity> getRoles() {
        List<RoleEntity> roles = roleRepository.findAll();
        log.info("Received {} roles", roles);
        return roles;
    }

    @Override
    public RoleEntity getRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new EntityNotFoundException(RoleEntity.ENTITY_NAME, "name", roleName.name()));
    }

    @Override
    public RoleEntity getRoleByProjectAndUser(ProjectEntity project, UserEntity user) {
        return user.getProjectRoles().get(project);
    }

    @Override
    public void changeUserRoleOnProject(Long projectId, Long userId, RoleName roleName) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.ENTITY_NAME, projectId));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, userId));
        changeUserRoleOnProject(project, user, roleName);
    }

    @Override
    public void changeUserRoleOnProject(ProjectEntity project, UserEntity user, RoleName roleName) {
        RoleEntity ownerRole = getRoleByName(roleName);
        user.getProjectRoles().put(project, ownerRole);
        userRepository.save(user);
        log.info("Changed user role on project. User id: {}, Project id: {}, Role name: {}",
                user.getId(), project.getId(), roleName.name());
    }
}
