package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.query.FilterSpecification;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.TaskDto;

import java.util.List;

public interface TaskService {

    List<TaskView> getTasks(int page, int limit, Long columnId, String filter);

    List<TaskView> getTasks(Long columnId, String filter);

    TaskView getTaskById(Long id);

    TaskView createTask(TaskEntity task, String username);

    TaskView updateTask(Long id, Long projectId, Long columnId, TaskDto taskDto);

    void deleteTask(Long id);

    FilterSpecification<TaskView> makeTasksFilter(String filter);
}
