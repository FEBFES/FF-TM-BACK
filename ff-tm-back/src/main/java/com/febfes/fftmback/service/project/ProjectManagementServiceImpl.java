package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.service.project.patch.ProjectPatchFieldProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("projectManagementService")
@Transactional
@RequiredArgsConstructor
public class ProjectManagementServiceImpl implements ProjectManagementService {

    private final ProjectRepository projectRepository;

    @Qualifier("projectPatchIsFavouriteProcessor")
    private final ProjectPatchFieldProcessor patchIsFavouriteProcessor;

    @Qualifier("projectPatchCommonProcessor")
    private final ProjectPatchFieldProcessor patchCommonProcessor;

    @PostConstruct
    private void postConstruct() {
        patchIsFavouriteProcessor.setNextProcessor(patchCommonProcessor);
    }

    @Override
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectEntity createProject(ProjectEntity project, Long userId) {
        project.setOwnerId(userId);
        ProjectEntity projectEntity = projectRepository.save(project);
        log.info("Created project with id={}", projectEntity.getId());
        return projectEntity;
    }

    @Override
    @Cacheable(value = "projects", key = "#id")
    public ProjectEntity getProject(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(Exceptions.projectNotFound(id));
        log.info("Received project by id={}", id);
        return projectEntity;
    }

    @Override
    @CacheEvict(value = "projects", key = "#id")
    public ProjectEntity editProject(Long id, ProjectEntity project) {
        ProjectEntity projectEntity = projectRepository.findById(id).orElseThrow(Exceptions.projectNotFound(id));
        projectEntity.setName(project.getName());
        projectEntity.setDescription(project.getDescription());
        projectRepository.save(projectEntity);
        log.info("Updated project with id={}", id);
        return projectEntity;
    }

    @Override
    @CacheEvict(value = "projects", key = "#id")
    public void editProjectPartially(Long id, Long ownerId, List<PatchDto> patchDtoList) {
        if (patchDtoList.isEmpty()) {
            return;
        }
        log.debug("Project with id={} partial update: {}", id, patchDtoList);
        ProjectEntity projectEntity = getProject(id);
        patchDtoList.forEach(patchDto -> patchIsFavouriteProcessor.patchField(projectEntity, ownerId, patchDto));
        projectRepository.save(projectEntity); // добавить бы какую-то проверку на то, надо ли обновлять проект или нет
        log.info("Project updated partially: {}", projectEntity);
    }

    @Override
    @CacheEvict(value = "projects", key = "#id")
    public void deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            log.info("Project with id={} was deleted", id);
        } else {
            throw new EntityNotFoundException(ProjectEntity.ENTITY_NAME, id);
        }
    }
}
