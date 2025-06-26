package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.common.UserProjectId;
import com.febfes.fftmback.domain.dao.UserProject;
import com.febfes.fftmback.domain.projection.MemberProjection;
import com.febfes.fftmback.domain.projection.ProjectForUserProjection;
import com.febfes.fftmback.domain.projection.ProjectProjection;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.ProjectForUserDto;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.repository.UserProjectRepository;
import com.febfes.fftmback.service.RoleService;
import com.febfes.fftmback.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.febfes.fftmback.util.CaseUtils.camelToSnake;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final ProjectMapper projectMapper;

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
        memberIds.forEach(memberId -> addUserToProjectAndChangeRole(projectId, memberId, RoleName.MEMBER));
        log.info("Added {} new members for project with id={}", memberIds.size(), projectId);
    }

    @Override
    @CacheEvict(value = "projects", allEntries = true)
    public MemberProjection removeMember(Long projectId, Long memberId) {
        MemberProjection memberToDelete = userService.getProjectMemberWithRole(projectId, memberId);
        userProjectRepository.deleteByIdProjectIdAndIdUserId(projectId, memberId);
        log.info("Removed member with id={} from project with id={}", memberId, projectId);
        return memberToDelete;
    }

    @Override
    @CacheEvict(value = "projects", allEntries = true)
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
