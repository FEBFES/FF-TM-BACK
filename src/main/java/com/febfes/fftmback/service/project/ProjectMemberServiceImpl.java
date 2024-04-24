package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.common.UserProjectId;
import com.febfes.fftmback.domain.dao.UserProject;
import com.febfes.fftmback.domain.projection.ProjectProjection;
import com.febfes.fftmback.domain.projection.ProjectWithMembersProjection;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.repository.UserProjectRepository;
import com.febfes.fftmback.service.RoleService;
import com.febfes.fftmback.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public List<ProjectDto> getProjectsForUser(Long userId, List<Sort.Order> sort) {
        List<Sort.Order> snakeCaseSort = sort.stream()
                .map(s -> new Sort.Order(s.getDirection(), camelToSnake(s.getProperty())))
                .toList();
        List<ProjectProjection> userProjects = projectRepository.getUserProjects(
                userId, Sort.by(snakeCaseSort));
        log.info("Received {} projects for user with id={}", userProjects.size(), userId);
        return userProjects.stream()
                .map(ProjectMapper.INSTANCE::projectProjectionToProjectDto)
                .toList();
    }

    @Override
    public OneProjectDto getProjectForUser(Long id, Long userId) {
        ProjectWithMembersProjection project = projectRepository.getProjectByIdAndUserId(id, userId)
                .orElseThrow(Exceptions.projectNotFound(id));
        log.info("Received project by id={} and userId={}", id, userId);
        List<MemberDto> members = userService.getProjectMembersWithRole(id);
        return ProjectMapper.INSTANCE.projectWithMembersProjectionToOneProjectDto(project, members);
    }

    @Override
    public List<MemberDto> addNewMembers(Long projectId, List<Long> memberIds) {
        memberIds.forEach(memberId -> addOrChangeProjectMemberRole(projectId, memberId, RoleName.MEMBER));
        log.info("Added {} new members for project with id={}", memberIds.size(), projectId);
        return userService.getProjectMembersWithRole(projectId, memberIds);
    }

    @Override
    public MemberDto removeMember(Long projectId, Long memberId) {
        MemberDto memberToDelete = userService.getProjectMemberWithRole(projectId, memberId);
        userProjectRepository.deleteByIdProjectIdAndIdUserId(projectId, memberId);
        log.info("Removed member with id={} from project with id={}", memberId, projectId);
        return memberToDelete;
    }

    @Override
    public void addOrChangeProjectMemberRole(Long projectId, Long memberId, RoleName roleName) {
        UserProject userProject = UserProject.builder()
                .id(UserProjectId.builder()
                        .userId(memberId)
                        .projectId(projectId)
                        .build())
                .build();
        userProjectRepository.save(userProject);
        roleService.changeUserRoleOnProject(projectId, memberId, roleName);
    }
}