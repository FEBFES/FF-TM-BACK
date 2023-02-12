package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.TaskEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.TaskRepository;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.util.DateProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final DateProvider dateProvider;
    private final UserService userService;

    @Override
    public List<TaskEntity> getTasks(
            int page,
            int limit,
            Long columnId
    ) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        List<TaskEntity> tasks = taskRepository.findAllByColumnId(pageableRequest, columnId)
                .stream()
                .toList();
        log.info("Received tasks size: {}", tasks.size());
        return tasks;
    }

    @Override
    public TaskEntity getTaskById(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TaskEntity.class.getSimpleName(), id));
        log.info("Received task: {}", task);
        return task;
    }

    @Override
    public TaskEntity createTask(
            Long projectId,
            Long columnId,
            TaskDto taskDto,
            String username
    ) {
        // TODO: use mapper
        TaskEntity task = TaskEntity.builder()
                .name(taskDto.name())
                .createDate(dateProvider.getCurrentDate())
                .description(taskDto.description())
                .columnId(columnId)
                .projectId(projectId)
                .ownerId(userService.getUserIdByUsername(username))
                .build();
        TaskEntity savedTask = taskRepository.save(task);
        log.info("Saved task: {}", savedTask);
        return savedTask;
    }

    @Override
    public TaskEntity updateTask(
            Long id,
            Long projectId,
            Long columnId,
            TaskDto taskDto
    ) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TaskEntity.class.getSimpleName(), id));
        task.setName(taskDto.name());
        task.setDescription(taskDto.description());
        task.setColumnId(columnId);
        task.setProjectId(projectId);
        taskRepository.save(task);
        log.info("Updated task: {}", task);
        return task;
    }

    @Override
    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            log.info("Task with id={} was deleted", id);
        } else {
            throw new EntityNotFoundException(TaskEntity.class.getSimpleName(), id);
        }
    }
}
