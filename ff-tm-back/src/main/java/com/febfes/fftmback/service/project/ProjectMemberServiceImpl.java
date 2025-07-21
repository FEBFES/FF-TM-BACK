package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.projection.MemberIdRoleProjection;
import com.febfes.fftmback.domain.projection.ProjectForUserProjection;
import com.febfes.fftmback.domain.projection.ProjectProjection;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.ProjectForUserDto;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.mapper.ProjectMemberMapper;
import com.febfes.fftmback.repository.ProjectMemberRepository;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.repository.UserProjectRepository;
import com.febfes.fftmback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.febfes.fftmback.util.CaseUtils.camelToSnake;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    private final ProjectMapper projectMapper;
    private final ProjectMemberMapper projectMemberMapper;

    private final UserService userService;
    private final ProjectMemberTransactionalService projectMemberTransactionalService;

    @Override
    @Cacheable(value = "projects", key = "'projectsForUser:' + #userId + ':' + #sort.hashCode()")
    public List<ProjectDto> getProjectsForUser(Long userId, List<Sort.Order> sort) {
        List<Sort.Order> snakeCaseSort = sort.stream()
                .map(s -> new Sort.Order(s.getDirection(), camelToSnake(s.getProperty())))
                .toList();
        List<ProjectProjection> userProjects = projectRepository.getUserProjects(userId, Sort.by(snakeCaseSort));
        log.info("Received {} projects for user with id={}", userProjects.size(), userId);
        return projectMapper.projectProjectionToProjectDto(userProjects);
    }

    @Override
    @Cacheable(value = "projects", key = "'projectForUser:' + #projectId + ':' + #userId")
    public ProjectForUserDto getProjectForUser(Long projectId, Long userId) {
        ProjectForUserProjection projectForUser = projectRepository.getProjectForUser(projectId, userId)
                .orElseThrow(Exceptions.projectNotFound(projectId));
        log.info("Received project by id={} and userId={}", projectId, userId);
        return new ProjectForUserDto(projectForUser);
    }

    @Override
    @CacheEvict(value = "projects", allEntries = true)
    public void addNewMembers(Long projectId, List<Long> memberIds) {
        memberIds.forEach(memberId ->
                projectMemberTransactionalService.addUserToProjectAndChangeRole(projectId, memberId, RoleName.MEMBER)
        );
        log.info("Added {} new members for project with id={}", memberIds.size(), projectId);
    }

    @Override
    @CacheEvict(value = "projects", allEntries = true)
    @Transactional
    public void removeMember(Long projectId, Long memberId) {
        userProjectRepository.deleteByIdProjectIdAndIdUserId(projectId, memberId);
        log.info("Removed member with id={} from project with id={}", memberId, projectId);
    }

    @Override
    @CacheEvict(value = "projects", allEntries = true)
    public void addUserToProjectAndChangeRole(Long projectId, Long memberId, RoleName roleName) {
        projectMemberTransactionalService.addUserToProjectAndChangeRole(projectId, memberId, roleName);
    }

    @Override
    public List<MemberDto> getProjectMembersWithRole(Long projectId) {
        List<MemberIdRoleProjection> projections = projectMemberRepository.getProjectMembersWithRole(projectId);
        Map<Long, UserDto> users = userService.getUsers(
                projections.stream().map(MemberIdRoleProjection::getId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserDto::id, Function.identity()));
        return projections.stream()
                .map(p -> projectMemberMapper.mapToMemberDto(p, users.get(p.getId())))
                .toList();
    }

    @Override
    public MemberDto getProjectMemberWithRole(Long projectId, Long memberId) {
        MemberIdRoleProjection projection = projectMemberRepository.getProjectMemberWithRole(projectId, memberId)
                .orElseThrow(Exceptions.userNotFoundById(memberId));
        UserDto user = userService.getUser(memberId);
        return projectMemberMapper.mapToMemberDto(projection, user);
    }

    @Override
    public List<MemberDto> getProjectMembersWithRole(Long projectId, Set<Long> membersIds) {
        List<MemberIdRoleProjection> projections = projectMemberRepository.getProjectMembersWithRole(projectId, membersIds);
        Map<Long, UserDto> users = userService.getUsers(membersIds)
                .stream()
                .collect(Collectors.toMap(UserDto::id, Function.identity()));
        return projections.stream()
                .map(p -> projectMemberMapper.mapToMemberDto(p, users.get(p.getId())))
                .toList();
    }
}
