package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.DashboardMapper;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.util.DateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final DateProvider dateProvider;
    private final ColumnService columnService;

    @Override
    public ProjectEntity createProject(ProjectDto projectDto) {
        ProjectEntity projectEntity = projectRepository.save(
                ProjectMapper.INSTANCE.projectDtoToProject(projectDto, dateProvider.getCurrentDate())
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
    public DashboardDto getDashboard(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id));
        log.info("Received dashboard for project with id={}", id);
        return DashboardMapper.INSTANCE.projectToDashboardDto(projectEntity);
    }
}
