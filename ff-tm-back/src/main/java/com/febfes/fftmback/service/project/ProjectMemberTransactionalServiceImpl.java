package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.common.UserProjectId;
import com.febfes.fftmback.domain.dao.UserProject;
import com.febfes.fftmback.repository.UserProjectRepository;
import com.febfes.fftmback.service.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectMemberTransactionalServiceImpl implements ProjectMemberTransactionalService {

    private final UserProjectRepository userProjectRepository;
    private final RoleService roleService;

    @Override
    public void addUserToProjectAndChangeRole(Long projectId, Long memberId, RoleName roleName) {
        UserProject userProject = UserProject.builder()
                .id(UserProjectId.builder()
                        .userId(memberId)
                        .projectId(projectId)
                        .build()
                )
                .build();

        userProjectRepository.save(userProject);

        roleService.changeUserRoleOnProject(projectId, memberId, roleName);
    }
}
