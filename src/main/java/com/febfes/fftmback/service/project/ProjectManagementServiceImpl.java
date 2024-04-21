package com.febfes.fftmback.service.project;

import com.febfes.fftmback.domain.common.PatchOperation;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.mapper.ProjectMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.service.project.patch.ProjectPatchFieldProcessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;

@Slf4j
@Service("projectManagementService")
@Transactional
@RequiredArgsConstructor
public class ProjectManagementServiceImpl implements ProjectManagementService {

    private final ProjectRepository projectRepository;

    private final ProjectPatchFieldProcessor patchIsFavouriteProcessor;

    @Override
    public ProjectEntity createProject(
            ProjectEntity project,
            Long userId
    ) {
        project.setOwnerId(userId);
        ProjectEntity projectEntity = projectRepository.save(project);
        log.info("Saved project: {}", projectEntity);
        return projectEntity;
    }

    @Override
    public ProjectEntity getProject(Long id) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(Exceptions.projectNotFound(id));
        log.info("Received project {} by id={}", projectEntity, id);
        return projectEntity;
    }

    @Override
    public ProjectDto editProject(Long id, ProjectEntity project) {
        ProjectEntity projectEntity = projectRepository.findById(id)
                .orElseThrow(Exceptions.projectNotFound(id));
        projectEntity.setName(project.getName());
        projectEntity.setDescription(project.getDescription());
        projectRepository.save(projectEntity);
        log.info("Updated project: {}", projectEntity);
        return ProjectMapper.INSTANCE.projectToProjectDto(projectEntity);
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
    public void deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            log.info("Project with id={} was deleted", id);
        } else {
            throw new EntityNotFoundException(ProjectEntity.ENTITY_NAME, id);
        }
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
            log.info(String.format("Can't find field \"%s\" in Project entity", patchDto.key()), e);
        }
    }
}
