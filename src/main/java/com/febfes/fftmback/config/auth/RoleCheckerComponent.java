package com.febfes.fftmback.config.auth;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.exception.RoleCheckException;
import com.febfes.fftmback.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.NavigableSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Component
@AllArgsConstructor
public class RoleCheckerComponent {

    private final UserRepository userRepository;

    private static final NavigableSet<RoleName> ROLE_HIERARCHY = new TreeSet<>((o1, o2) -> {
        if (o1.getPriority() > o2.getPriority()) {
            return 1;
        } else if (o1.getPriority() < o2.getPriority()) {
            return -1;
        }
        return 0;
    });

    @PostConstruct
    private void postConstruct() {
        ROLE_HIERARCHY.add(RoleName.OWNER);
        ROLE_HIERARCHY.add(RoleName.MEMBER_PLUS);
        ROLE_HIERARCHY.add(RoleName.MEMBER);
    }

    public void checkIfHasRole(Long projectId, RoleName roleName) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleEntity userRole = getUserRole(user, projectId);
        if (!hasRole(userRole, roleName)) {
            throw new RoleCheckException(roleName, userRole.getName());
        }
    }

    public void checkIfUserIsOwner(Long projectId, Long userId) {
        UserEntity userToCheck = userRepository.findById(userId)
                .orElseThrow(Exceptions.userNotFoundById(userId));
        UserEntity loggedUser = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleEntity loggedUserRole = getUserRole(loggedUser, projectId);
        if (hasRole(getUserRole(userToCheck, projectId), RoleName.OWNER)) {
            throw new RoleCheckException(RoleName.OWNER, loggedUserRole.getName());
        }
    }

    private RoleEntity getUserRole(UserEntity user, Long projectId) {
        return Optional.ofNullable(user.getProjectRoles().get(projectId))
                .orElseThrow(Exceptions.roleNotFoundByProjectId(projectId));
    }

    private boolean hasRole(RoleEntity userRole, RoleName roleName) {
        Set<RoleName> belowNecessary = ROLE_HIERARCHY.tailSet(roleName);
        return belowNecessary.contains(userRole.getName());
    }
}
