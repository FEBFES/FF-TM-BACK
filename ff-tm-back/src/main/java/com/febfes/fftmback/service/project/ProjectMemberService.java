package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.projection.MemberProjection;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.ProjectForUserDto;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public interface ProjectMemberService {

    List<ProjectDto> getProjectsForUser(Long userId, List<Sort.Order> sort);

    ProjectForUserDto getProjectForUser(Long projectId, Long userId);

    void addNewMembers(Long projectId, List<Long> memberIds);

    MemberProjection removeMember(Long projectId, Long memberId);

    void addUserToProjectAndChangeRole(Long projectId, Long memberId, RoleName roleName);

    List<MemberProjection> getProjectMembersWithRole(Long projectId);

    MemberProjection getProjectMemberWithRole(Long projectId, Long memberId);

    List<MemberProjection> getProjectMembersWithRole(Long projectId, Set<Long> membersIds);
}
