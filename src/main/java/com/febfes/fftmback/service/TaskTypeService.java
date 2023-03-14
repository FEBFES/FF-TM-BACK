package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.TaskTypeEntity;

import java.util.List;
import java.util.Optional;

public interface TaskTypeService {

    void createDefaultTaskTypesForProject(Long projectId);

    List<TaskTypeEntity> getTaskTypesByProjectId(Long projectId);

    Optional<TaskTypeEntity> getTaskTypeByNameAndProjectId(String name, Long projectId);

    void deleteAllTypesByProjectId(Long projectId);

    TaskTypeEntity createTaskType(TaskTypeEntity taskType);
}
