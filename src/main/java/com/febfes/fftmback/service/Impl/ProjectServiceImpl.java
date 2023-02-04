package com.febfes.fftmback.service.Impl;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.DashboardMapper;
import com.febfes.fftmback.repository.ProjectRepository;
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

    public ProjectEntity createProject(ProjectDto projectDto) {
        ProjectEntity projectEntity = projectRepository.save(
                createProjectEntity(projectDto.name(), projectDto.description())
        );
        log.info("Save project: {}", projectEntity);
        return projectEntity;
    }

    public List<ProjectEntity> getProjects() {
        List<ProjectEntity> projectEntityList = projectRepository.findAll();
        log.info("Received {} projects", projectEntityList.size());
        return projectEntityList;
    }

    public ProjectEntity getProject(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id));
        log.info("Received project {} by id= {}", projectEntity, id);
        return projectEntity;
    }

    public void editProject(Long id, ProjectDto projectDto) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id));
        projectEntity.setName(projectDto.name());
        projectEntity.setDescription(projectDto.description());
        projectRepository.save(projectEntity);
        log.info("Updated project: {}", projectEntity);
    }

    public void deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            log.info("Project with id= {} was deleted", id);
        } else {
            throw new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id);
        }

    }

    public DashboardDto getDashboard(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id));
        log.info("Received dashboard for project with id=", id);
        return DashboardMapper.INSTANCE.projectToDashboardDto(projectEntity);
    }

    private ProjectEntity createProjectEntity(String name, String description) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setName(name);
        projectEntity.setDescription(description);
        projectEntity.setCreateDate(dateProvider.getCurrentDate());
        return projectEntity;
    }

}
