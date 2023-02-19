package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.ColumnWithTasksMapper;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
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

    @Override
    public ProjectEntity createProject(
            ProjectDto projectDto,
            String username
    ) {
        ProjectEntity projectEntity = projectRepository.save(
                ProjectMapper.INSTANCE.projectDtoToProject(
                        projectDto,
                        DateUtils.getCurrentDate(),
                        userService.getUserIdByUsername(username)
                )
        );
        log.info("Saved project: {}", projectEntity);
        columnService.createDefaultColumnsForProject(projectEntity.getId());
        return projectEntity;
    }

    @Override
    public List<ProjectEntity> getProjects() {
        List<ProjectEntity> projectEntityList = projectRepository.findAll();
        log.info("Received {} projects", projectEntityList.size());
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
    public void editProject(Long id, ProjectDto projectDto) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id));
        projectEntity.setName(projectDto.name());
        projectEntity.setDescription(projectDto.description());
        projectRepository.save(projectEntity);
        log.info("Updated project: {}", projectEntity);
    }

    @Override
    public void deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
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
