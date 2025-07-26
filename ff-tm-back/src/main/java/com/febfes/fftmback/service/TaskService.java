package com.febfes.fftmback.service;

import com.febfes.fftmback.config.jwt.User;
import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskShortDto;

import java.util.List;
import java.util.Set;

public interface TaskService {

    List<TaskShortDto> getTasks(int page, int limit, Long columnId, TaskSpec taskSpec);

    List<TaskView> getTasks(Set<Long> columnId, TaskSpec taskSpec);

    TaskDto getTaskById(Long id);

    TaskShortDto getTaskShortById(Long id);

    /**
     * Creates task with parameters from task.
     *
     * @param task task to create
     * @param user user that creates task
     * @return created task id
     */
    Long createTask(TaskEntity task, User user);

    void updateTask(TaskEntity editTask, User user);

    void deleteTask(Long id);
}
