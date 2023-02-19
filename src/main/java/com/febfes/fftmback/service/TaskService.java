package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.TaskDto;

import java.util.List;

public interface TaskService {

    List<TaskEntity> getTasks(int page, int limit, Long columnId, String filter);

    TaskEntity getTaskById(Long id);

    TaskEntity createTask(Long projectId, Long columnId, TaskDto taskDto, String username);

    TaskEntity updateTask(Long id, Long projectId, Long columnId, TaskDto taskDto);

    void deleteTask(Long id);
}
