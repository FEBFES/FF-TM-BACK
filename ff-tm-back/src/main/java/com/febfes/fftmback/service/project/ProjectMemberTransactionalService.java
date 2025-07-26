package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.RoleName;

public interface ProjectMemberTransactionalService {

    void addUserToProjectAndChangeRole(Long projectId, Long memberId, RoleName roleName);
}
