package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskView;

import java.util.List;
import java.util.Set;

public interface TaskService {

    List<TaskView> getTasks(int page, int limit, Long columnId, TaskSpec taskSpec);

    List<TaskView> getTasks(Set<Long> columnId, TaskSpec taskSpec);

    TaskView getTaskById(Long id);

    /**
     * Creates task with parameters from task.
     *
     * @param task task to create
     * @param userId user id that creates task
     * @return created task id
     */
    Long createTask(TaskEntity task, Long userId);

    void updateTask(TaskEntity editTask);

    void deleteTask(Long id);
}
