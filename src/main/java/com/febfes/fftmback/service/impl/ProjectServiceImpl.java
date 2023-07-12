package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.PatchOperation;
import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.dto.RoleDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.ColumnWithTasksMapper;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.mapper.RoleMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.service.*;
import com.febfes.fftmback.util.patch.ProjectPatchFieldProcessor;
import com.febfes.fftmback.util.patch.ProjectPatchIsFavouriteProcessor;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
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
            String username
    ) {
        Long ownerId = userService.getUserIdByUsername(username);
        project.setOwnerId(ownerId);
        ProjectEntity projectEntity = projectRepository.save(project);
        log.info("Saved project: {}", projectEntity);
        Long projectId = projectEntity.getId();
        columnService.createDefaultColumnsForProject(projectId);
        taskTypeService.createDefaultTaskTypesForProject(projectId);
        // by default, the owner will also be a member of the project
        addOwnerToProjectMembers(project, ownerId);
        return projectEntity;
    }

    @Override
    public List<ProjectEntity> getProjectsForUser(Long userId) {
        UserEntity user = userService.getUserById(userId);
        List<ProjectEntity> userProjects = user.getProjects().stream()
                .peek(project -> project.setIsFavourite(projectRepository.isProjectFavourite(project.getId(), userId)))
                .toList();
        log.info("Received {} projects for user with id={}", userProjects.size(), userId);
        return userProjects;
    }

    @Override
    public ProjectEntity getProject(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.ENTITY_NAME, id));
        log.info("Received project {} by id={}", projectEntity, id);
        return projectEntity;
    }

    @Override
    public OneProjectDto getProjectForUser(Long id, Long userId) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.ENTITY_NAME, id));
        projectEntity.setIsFavourite(projectRepository.isProjectFavourite(id, userId));
        log.info("Received project {} by id={} and userId={}", projectEntity, id, userId);
        UserEntity user = userService.getUserById(userId);
        RoleDto userRoleOnProject = RoleMapper.INSTANCE.roleToRoleDto(
                roleService.getRoleByProjectAndUser(id, user)
        );
        return ProjectMapper.INSTANCE.projectToOneProjectDto(projectEntity, userRoleOnProject);
    }

    @Override
    public void editProject(Long id, ProjectEntity project) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.ENTITY_NAME, id));
        projectEntity.setName(project.getName());
        projectEntity.setDescription(project.getDescription());
        projectRepository.save(projectEntity);
        log.info("Updated project: {}", projectEntity);
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
    public DashboardDto getDashboard(Long id, String taskFilter) {
        return new DashboardDto(
                columnService.getOrderedColumns(id)
                        .stream()
                        .map(column -> {
                            // TODO: maybe we can optimize it somehow?
//                            List<TaskView> filteredTasks = taskService.getTasks(column.getId(), taskFilter);
                            return ColumnWithTasksMapper.INSTANCE.columnToColumnWithTasksDto(column, new ArrayList<>());
                        })
                        .toList()
        );
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
        if (!projectRepository.isProjectFavourite(projectId, userId)) {
            projectRepository.addProjectToFavourite(projectId, userId);
        }
    }

    @Override
    public void removeProjectFromFavourite(Long projectId, Long userId) {
        projectRepository.removeProjectFromFavourite(projectId, userId);
    }

    @Override
    public Set<UserEntity> getProjectMembers(Long projectId) {
        ProjectEntity project = getProject(projectId);
        return project.getMembers();
    }

    @Override
    public List<UserEntity> addNewMembers(Long projectId, List<Long> memberIds) {
        ProjectEntity project = getProject(projectId);
        List<UserEntity> addedMembers = new ArrayList<>();
        memberIds.forEach(memberId -> {
            UserEntity member = userService.getUserById(memberId);
            roleService.changeUserRoleOnProject(projectId, member, RoleName.MEMBER);
            project.addMember(member);
            addedMembers.add(member);
        });
        projectRepository.save(project);
        log.info("Added {} new members for project with id={}", memberIds.size(), projectId);
        return addedMembers;
    }

    @Override
    public UserEntity removeMember(Long projectId, Long memberId) {
        ProjectEntity project = getProject(projectId);
        project.removeMember(memberId);
        projectRepository.save(project);
        log.info("Removed member with id={} from project with id={}", memberId, projectId);
        return userService.getUserById(memberId);
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
            e.printStackTrace();
            log.info("Can't find field \"{}\" in Project entity", patchDto.key());
        }
    }

    private void addOwnerToProjectMembers(ProjectEntity project, Long ownerId) {
        UserEntity owner = userService.getUserById(ownerId);
        roleService.changeUserRoleOnProject(project.getId(), owner, RoleName.OWNER);
        project.addMember(owner);
        projectRepository.save(project);
    }

}
