package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.projection.MemberProjection;
import com.febfes.fftmback.domain.projection.ProjectForUserProjection;
import com.febfes.fftmback.domain.projection.ProjectProjection;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProjectMemberService {

    List<ProjectProjection> getProjectsForUser(Long userId, List<Sort.Order> sort);

    ProjectForUserProjection getProjectForUser(Long projectId, Long userId);

    void addNewMembers(Long projectId, List<Long> memberIds);

    MemberProjection removeMember(Long projectId, Long memberId);

    void addUserToProjectAndChangeRole(Long projectId, Long memberId, RoleName roleName);
}
