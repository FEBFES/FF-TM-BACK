package com.example.fftmback.service;

import com.example.fftmback.domain.ProjectEntity;
import com.example.fftmback.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    public static final ProjectEntity EMPTY_PROJECT_ENTITY = new ProjectEntity();
    @Autowired
    protected ProjectRepository projectRepository;


    public ProjectEntity createProject(String name, String description) {
        return projectRepository.save(createProjectEntity(name, description));
    }

    public List<ProjectEntity> getProjects() {
        // TODO add pagination
        return projectRepository.findAll();
    }

    public ProjectEntity getProject(Long id) {
        return projectRepository.findById(id).orElse(EMPTY_PROJECT_ENTITY);
    }

    public boolean editProject(Long id, String name, String description) {
        Optional<ProjectEntity> projectEntity = projectRepository.findById(id);
        projectEntity.ifPresent(project -> {
            project.setName(name);
            project.setDescription(description);
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
        projectEntity.setCreateDate(new Date());
        return projectEntity;
    }

    public static boolean isEmptyProject(ProjectEntity projectEntity) {
        return projectEntity.getId() == null;
    }


}
