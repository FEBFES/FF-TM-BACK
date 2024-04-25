package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.ProjectDto;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProjectMemberService {

    List<ProjectDto> getProjectsForUser(Long userId, List<Sort.Order> sort);

    OneProjectDto getProjectForUser(Long projectId, Long userId);

    void addNewMembers(Long projectId, List<Long> memberIds);

    MemberDto removeMember(Long projectId, Long memberId);

    void addUserToProjectAndChangeRole(Long projectId, Long memberId, RoleName roleName);
}
