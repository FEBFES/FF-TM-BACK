package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.PatchOperation;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.ProjectOwnerException;
import com.febfes.fftmback.mapper.ColumnWithTasksMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.service.UserService;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ColumnService columnService;
    private final UserService userService;
    private final TaskTypeService taskTypeService;

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
        project.setOwnerId(userService.getUserIdByUsername(username));
        ProjectEntity projectEntity = projectRepository.save(project);
        log.info("Saved project: {}", projectEntity);
        Long projectId = projectEntity.getId();
        columnService.createDefaultColumnsForProject(projectId);
        taskTypeService.createDefaultTaskTypesForProject(projectId);
        return projectEntity;
    }

    @Override
    public List<ProjectEntity> getProjectsForUser(Long userId) {
        UserEntity user = userService.getUserById(userId);
        Set<Long> favouriteProjects = getFavouriteProjectsForUser(userId);
        // TODO Возможно обработку является ли проект избранным стоит вынести в запрос
        List<ProjectEntity> ownedProjects = projectRepository
                .findAllByOwnerId(userId)
                .stream()
                .peek(project -> project.setIsFavourite(favouriteProjects.contains(project.getId())))
                .toList();
        List<ProjectEntity> userProjects = new ArrayList<>(ownedProjects);
        userProjects.addAll(user.getProjects());
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
    public ProjectEntity getProjectForUser(Long id, Long userId) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.ENTITY_NAME, id));
        projectEntity.setIsFavourite(projectRepository.isProjectFavourite(id, userId));
        log.info("Received project {} by id={} and userId={}", projectEntity, id, userId);
        return projectEntity;
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
                columnService.getColumnListWithOrder(id, taskFilter)
                        .stream()
                        .map(ColumnWithTasksMapper.INSTANCE::columnToColumnWithTasksDto)
                        .collect(Collectors.toList())
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
    public List<UserEntity> addNewMembers(Long projectId, List<Long> memberIds, Long ownerId) {
        ProjectEntity project = getProject(projectId);
        if (!Objects.equals(project.getOwnerId(), ownerId)) {
            throw new ProjectOwnerException(project.getOwnerId());
        }
        if (memberIds.contains(ownerId)) {
            throw new ProjectOwnerException();
        }
        List<UserEntity> addedMembers = new ArrayList<>();
        memberIds.forEach(memberId -> {
            UserEntity member = userService.getUserById(memberId);
            project.addMember(member);
            addedMembers.add(member);
        });
        projectRepository.save(project);
        log.info("Added {} new members for project with id={}", memberIds.size(), projectId);
        return addedMembers;
    }

    @Override
    public UserEntity removeMember(Long projectId, Long memberId, Long ownerId) {
        ProjectEntity project = getProject(projectId);
        if (!Objects.equals(project.getOwnerId(), ownerId)) {
            throw new ProjectOwnerException(project.getOwnerId());
        }
        project.removeMember(memberId);
        projectRepository.save(project);
        log.info("Removed member with id={} from project with id={}", memberId, projectId);
        return userService.getUserById(memberId);
    }

    private Set<Long> getFavouriteProjectsForUser(Long userId) {
        return projectRepository.findAllFavouriteProjectIdsForUser(userId);
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

}
