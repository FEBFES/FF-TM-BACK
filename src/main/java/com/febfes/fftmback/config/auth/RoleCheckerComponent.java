package com.febfes.fftmback.config.auth;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.RoleCheckException;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

@Component
public class RoleCheckerComponent {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private static final NavigableSet<RoleName> ROLE_HIERARCHY = new TreeSet<>((o1, o2) -> {
        if (o1.getPriority() > o2.getPriority()) {
            return 1;
        } else if (o1.getPriority() < o2.getPriority()) {
            return -1;
        }
        return 0;
    });

    public RoleCheckerComponent(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        ROLE_HIERARCHY.add(RoleName.OWNER);
        ROLE_HIERARCHY.add(RoleName.MEMBER_PLUS);
        ROLE_HIERARCHY.add(RoleName.MEMBER);
    }

    private boolean hasRole(UserEntity user, Long projectId, RoleName roleName) {
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.ENTITY_NAME, projectId));
        Set<RoleName> belowNecessary = ROLE_HIERARCHY.tailSet(roleName);
        RoleEntity userRole = user.getProjectRoles().get(project.getId());
        return belowNecessary.contains(userRole.getName());
    }

    public void checkIfHasRole(Long projectId, RoleName roleName) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!hasRole(user, projectId, roleName)) {
            throw new RoleCheckException(roleName.name());
        }
    }

    public boolean userHasRole(Long projectId, Long userId, RoleName roleName) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.ENTITY_NAME, userId));
        return hasRole(user, projectId, roleName);
    }
}
