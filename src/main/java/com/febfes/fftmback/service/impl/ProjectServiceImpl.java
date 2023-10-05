package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.PatchOperation;
import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.common.UserProjectId;
import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.domain.dao.UserProject;
import com.febfes.fftmback.domain.projection.ProjectProjection;
import com.febfes.fftmback.domain.projection.ProjectWithMembersProjection;
import com.febfes.fftmback.dto.*;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.mapper.ColumnWithTasksMapper;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.repository.UserProjectRepository;
import com.febfes.fftmback.service.*;
import com.febfes.fftmback.util.patch.ProjectPatchFieldProcessor;
import com.febfes.fftmback.util.patch.ProjectPatchIsFavouriteProcessor;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

import static com.febfes.fftmback.util.CaseUtils.camelToSnake;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final ColumnService columnService;
    private final UserService userService;
    private final TaskTypeService taskTypeService;
    private final RoleService roleService;
    private final TaskService taskService;

    private ProjectPatchFieldProcessor patchIsFavouriteProcessor;

    @PostConstruct
    private void postConstruct() {
        patchIsFavouriteProcessor = new ProjectPatchIsFavouriteProcessor(this, null);
    }

    @Override
    public ProjectEntity createProject(
            ProjectEntity project,
            Long userId
    ) {
        project.setOwnerId(userId);
        ProjectEntity projectEntity = projectRepository.save(project);
        log.info("Saved project: {}", projectEntity);
        Long projectId = projectEntity.getId();
        columnService.createDefaultColumnsForProject(projectId);
        taskTypeService.createDefaultTaskTypesForProject(projectId);
        // by default, the owner will also be a member of the project
        addOrChangeProjectMemberRole(project.getId(), userId, RoleName.OWNER);
        return projectEntity;
    }

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
    public ProjectEntity getProject(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(Exceptions.projectNotFound(id));
        log.info("Received project {} by id={}", projectEntity, id);
        return projectEntity;
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
    public ProjectDto editProject(Long id, ProjectEntity project) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(Exceptions.projectNotFound(id));
        projectEntity.setName(project.getName());
        projectEntity.setDescription(project.getDescription());
        projectRepository.save(projectEntity);
        log.info("Updated project: {}", projectEntity);
        return ProjectMapper.INSTANCE.projectToProjectDto(projectEntity);
    }

    @Override
    public void deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            taskTypeService.deleteAllTypesByProjectId(id);
            projectRepository.deleteById(id);
            log.info("Project with id={} was deleted", id);
        } else {
            throw new EntityNotFoundException(ProjectEntity.ENTITY_NAME, id);
        }
    }

    @Override
    public DashboardDto getDashboard(Long id, TaskSpec taskSpec) {
        List<ColumnWithTasksDto> columnsWithTasks = columnService.getOrderedColumns(id)
                .stream()
                .map(column -> {
                    List<TaskView> filteredTasks = taskService.getTasks(column.getId(), taskSpec);
                    return ColumnWithTasksMapper.INSTANCE.columnToColumnWithTasksDto(column, filteredTasks);
                })
                .toList();
        return new DashboardDto(columnsWithTasks);
    }

    @Override
    public void editProjectPartially(
            Long id,
            Long ownerId,
            List<PatchDto> patchDtoList
    ) {
        log.info("Project with id={} partial update: {}", id, patchDtoList);
        ProjectEntity projectEntity = getProject(id);
        patchDtoList.forEach(patchDto -> {
            patchIsFavouriteProcessor.patchField(id, ownerId, patchDto);
            if (PatchOperation.getByCode(patchDto.op()).equals(PatchOperation.UPDATE)) {
                updateProjectField(patchDto, projectEntity);
            }
        });
        projectRepository.save(projectEntity);
        log.info("Project updated partially: {}", projectEntity);
    }

    @Override
    public void addProjectToFavourite(Long projectId, Long userId) {
        if (Boolean.FALSE.equals(projectRepository.isProjectFavourite(projectId, userId))) {
            projectRepository.addProjectToFavourite(projectId, userId);
        }
    }

    @Override
    public void removeProjectFromFavourite(Long projectId, Long userId) {
        projectRepository.removeProjectFromFavourite(projectId, userId);
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

    private void updateProjectField(
            PatchDto patchDto,
            ProjectEntity projectEntity
    ) {
        try {
            Field field = projectEntity.getClass().getDeclaredField(patchDto.key());
            field.setAccessible(true);
            ReflectionUtils.setField(field, projectEntity, patchDto.value());
        } catch (NoSuchFieldException e) {
            log.info(String.format("Can't find field \"%s\" in Project entity", patchDto.key()), e);
        }
    }

    private void addOrChangeProjectMemberRole(Long projectId, Long memberId, RoleName roleName) {
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
