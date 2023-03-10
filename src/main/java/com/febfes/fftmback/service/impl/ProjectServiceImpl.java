package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.ColumnWithTasksMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.util.DateUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
        project.setCreateDate(DateUtils.getCurrentDate());
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
        List<ProjectEntity> projectEntityList = projectRepository.findAllByOwnerId(ownerId);
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
}
