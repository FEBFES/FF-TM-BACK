package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.repository.TaskTypeRepository;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TaskTypeServiceImpl implements TaskTypeService {


    @Autowired
    private TaskTypeRepository taskTypeRepository;

    @Override
    public void createDefaultTaskTypesForProject(Long projectId) {
        DEFAULT_TASK_TYPES
                .stream()
                .map(typeName -> TaskTypeEntity
                        .builder()
                        .name(typeName)
                        .createDate(DateUtils.getCurrentDate())
                        .projectId(projectId)
                        .build()
                )
                .forEach(taskTypeEntity -> taskTypeRepository.save(taskTypeEntity));
        log.info("Created default columns with names: {}", DEFAULT_TASK_TYPES);
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


}
