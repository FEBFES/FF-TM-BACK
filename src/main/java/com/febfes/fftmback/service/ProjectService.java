package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.mapper.DashboardMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.util.DateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DateProvider dateProvider;

    public ProjectEntity createProject(ProjectDto projectDto) {
        return projectRepository.save(createProjectEntity(projectDto.name(), projectDto.description()));
    }

    public List<ProjectEntity> getProjects() {
        return projectRepository.findAll();
    }

    public Optional<ProjectEntity> getProject(Long id) {
        return projectRepository.findById(id);
    }

    public void editProject(Long id, ProjectDto projectDto) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id));
        projectEntity.setName(projectDto.name());
        projectEntity.setDescription(projectDto.description());
        projectRepository.save(projectEntity);
    }

    public void deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id);
        }
    }

    public DashboardDto getDashboard(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProjectEntity.class.getSimpleName(), id));
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
