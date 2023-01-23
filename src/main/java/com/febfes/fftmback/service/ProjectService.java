package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.util.DateProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    public static final ProjectEntity EMPTY_PROJECT_ENTITY = new ProjectEntity();

    private final ProjectRepository projectRepository;
    private final DateProvider dateProvider;

    public ProjectEntity createProject(ProjectDto projectDto) {
        return projectRepository.save(createProjectEntity(projectDto.getName(), projectDto.getDescription()));
    }

    public List<ProjectEntity> getProjects() {
        // TODO add pagination
        return projectRepository.findAll();
    }

    public ProjectEntity getProject(Long id) {
        return projectRepository.findById(id).orElse(EMPTY_PROJECT_ENTITY);
    }

    public boolean editProject(Long id, ProjectDto projectDto) {
        Optional<ProjectEntity> projectEntity = projectRepository.findById(id);
        projectEntity.ifPresent(project -> {
            project.setName(projectDto.getName());
            project.setDescription(projectDto.getDescription());
            projectRepository.save(project);
        });
        return projectEntity.isPresent();
    }

    public boolean deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
        }
        return true;
    }

    private ProjectEntity createProjectEntity(String name, String description) {
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setName(name);
        projectEntity.setDescription(description);
        projectEntity.setCreateDate(dateProvider.getCurrentDate());
        return projectEntity;
    }

    public static boolean isEmptyProject(ProjectEntity projectEntity) {
        return projectEntity.getId() == null;
    }

    public static ProjectDto mapToProjectDto(ProjectEntity projectEntity) {
        return new ProjectDto(
                projectEntity.getId(),
                projectEntity.getName(),
                projectEntity.getDescription(),
                projectEntity.getCreateDate()
        );
    }


}