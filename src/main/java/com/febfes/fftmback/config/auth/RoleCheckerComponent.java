package com.febfes.fftmback.config.auth;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.exception.RoleCheckException;
import com.febfes.fftmback.service.ProjectService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

@Component
@RequiredArgsConstructor
public class RoleCheckerComponent {

    private static NavigableSet<RoleName> ROLE_HIERARCHY;

    private final ProjectService projectService;

    @PostConstruct
    private void postConstruct() {
        Comparator<RoleName> roleComparator = (o1, o2) -> {
            if (o1.getPriority() > o2.getPriority()) {
                return 1;
            } else if (o1.getPriority() < o2.getPriority()) {
                return -1;
            }
            return 0;
        };
        ROLE_HIERARCHY = new TreeSet<>(roleComparator);
        ROLE_HIERARCHY.add(RoleName.OWNER);
        ROLE_HIERARCHY.add(RoleName.MEMBER_PLUS);
        ROLE_HIERARCHY.add(RoleName.MEMBER);
    }

    private boolean hasRole(Long projectId, RoleName roleName) {
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProjectEntity project = projectService.getProject(projectId);
        Set<RoleName> belowNecessary = ROLE_HIERARCHY.tailSet(roleName);
        RoleEntity userRole = user.getProjectRoles().get(project);
        RoleName userRoleName = RoleName.valueOf(userRole.getName());
        return belowNecessary.contains(userRoleName);
    }

    public void checkIfHasRole(Long projectId, RoleName roleName) {
        if (!hasRole(projectId, roleName)) {
            throw new RoleCheckException(roleName.name());
        }
    }
}
