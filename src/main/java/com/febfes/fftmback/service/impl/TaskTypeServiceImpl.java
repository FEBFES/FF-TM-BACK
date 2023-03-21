package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.repository.TaskTypeRepository;
import com.febfes.fftmback.service.TaskTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskTypeServiceImpl implements TaskTypeService {

    public final List<String> DEFAULT_TASK_TYPES = List.of("bug", "feature", "research", "question");


    private final TaskTypeRepository taskTypeRepository;

    @Override
    public void createDefaultTaskTypesForProject(Long projectId) {
        DEFAULT_TASK_TYPES
                .stream()
                .map(typeName -> TaskTypeEntity
                        .builder()
                        .name(typeName)
                        .projectId(projectId)
                        .build()
                )
                .forEach(taskTypeRepository::save);
        log.info("Created default task types: {} for project with id={}", DEFAULT_TASK_TYPES, projectId);
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
    }

    @Override
    public TaskTypeEntity createTaskType(TaskTypeEntity taskType) {
        return taskTypeRepository.save(taskType);
    }


}
