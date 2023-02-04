package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.TaskEntity;
import com.febfes.fftmback.dto.TaskDto;

import java.util.List;

public interface TaskService {

    List<TaskEntity> getTasks(int page, int limit, Long columnId);

    TaskEntity getTaskById(Long id);

    TaskEntity createTask(Long projectId, Long columnId, TaskDto taskDto);

    TaskEntity updateTask(Long id, Long projectId, Long columnId, TaskDto taskDto);

    void deleteTask(Long id);
}
