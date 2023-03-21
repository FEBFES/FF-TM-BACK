package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.ProjectSettingsDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.ColumnWithTasksMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public List<ProjectEntity> getProjectsByOwnerId(Long ownerId) {
        Set<Long> favouriteProjects = getFavouriteProjectsForUser(ownerId);
        // TODO Возможно обработку является ли проект избранным стоит вынести в запрос
        List<ProjectEntity> projectEntityList = projectRepository
                .findAllByOwnerId(ownerId)
                .stream()
                .peek(project -> project.setIsFavourite(favouriteProjects.contains(project.getId())))
                .toList();
        log.info("Received {} projects for owner with id={}", projectEntityList.size(), ownerId);
        return projectEntityList;
    }

    @Override
    public ProjectEntity getProject(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id));
        log.info("Received project {} by id={}", projectEntity, id);
        return projectEntity;
    }

    @Override
    public void editProject(Long id, ProjectEntity project) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id));
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
            throw new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id);
        }
    }

    @Override
    public DashboardDto getDashboard(Long id, String taskFilter) {
        return new DashboardDto(
                columnService
                        .getColumnListWithOrder(id, taskFilter)
                        .stream()
                        .map(ColumnWithTasksMapper.INSTANCE::columnToColumnWithTasksDto)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void setProjectFavouriteStatus(ProjectSettingsDto projectSettings) {
        Boolean isFavourite = projectSettings.isFavourite();
        Long projectId = projectSettings.projectId();
        Long userId = projectSettings.userId();
        if (Objects.isNull(isFavourite) || Objects.isNull(projectId) || Objects.isNull(userId)) {
            return;
        }
        if (isFavourite) {
            addProjectToFavourite(projectId, userId);
        } else {
            removeProjectFromFavourite(projectId, userId);
        }

    }

    private void addProjectToFavourite(Long projectId, Long userId) {
        if (!projectRepository.isProjectFavourite(projectId, userId)) {
            projectRepository.addProjectToFavourite(projectId, userId);
        }
    }

    private void removeProjectFromFavourite(Long projectId, Long userId) {
        projectRepository.removeProjectFromFavourite(projectId, userId);
    }

    private Set<Long> getFavouriteProjectsForUser(Long userId) {
        return projectRepository.findAllFavouriteProjectIdsForUser(userId);
    }

}
