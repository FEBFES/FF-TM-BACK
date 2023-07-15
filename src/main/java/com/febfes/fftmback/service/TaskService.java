package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.EditTaskDto;

import java.util.List;

public interface TaskService {

    List<TaskView> getTasks(int page, int limit, Long columnId, TaskSpec taskSpec);

    List<TaskView> getTasks(Long columnId, TaskSpec taskSpec);

    TaskView getTaskById(Long id);

    TaskView createTask(TaskEntity task, String username);

    TaskView updateTask(Long id, Long projectId, Long columnId, EditTaskDto editTaskDto);

    void deleteTask(Long id);
}
