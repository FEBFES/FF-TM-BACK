package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.repository.TaskTypeRepository;
import com.febfes.fftmback.service.TaskTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskTypeServiceImpl implements TaskTypeService {

    private final TaskTypeRepository taskTypeRepository;

    @Override
    public void createDefaultTaskTypesForProject(Long projectId) {
        Arrays.stream(DefaultTaskTypes.values())
                .map(type -> TaskTypeEntity.builder()
                        .name(type.getCaption())
                        .projectId(projectId)
                        .build()
                )
                .forEach(taskTypeRepository::save);
        log.info("Created default task types for project with id={}", projectId);
    }

    @Override
    public Optional<TaskTypeEntity> getTaskTypeByNameAndProjectId(String name, Long projectId) {
        return taskTypeRepository.findByNameAndProjectId(name, projectId);
    }

    @Override
    public List<TaskTypeEntity> getTaskTypesByProjectId(Long projectId) {
        return taskTypeRepository.findAllByProjectId(projectId);
    }

    @Override
    public void deleteAllTypesByProjectId(Long projectId) {
        taskTypeRepository.deleteAllByProjectId(projectId);
        log.info("All project {} task types were removed", projectId);
    }

    @Override
    public TaskTypeEntity createTaskType(TaskTypeEntity taskType) {
        log.info("Task type created: {}", taskType);
        return taskTypeRepository.save(taskType);
    }


}
