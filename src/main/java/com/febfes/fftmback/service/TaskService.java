package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.TaskEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskShortDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.TaskRepository;
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
public class TaskService {

    private final TaskRepository taskRepository;
    private final DateProvider dateProvider;

    public List<TaskEntity> getTasks(int page, int limit) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        List<TaskEntity> tasks = taskRepository.findAll(pageableRequest).stream().toList();
        log.info("Received tasks size: {}", tasks.size());
        return tasks;
    }

    public TaskEntity getTaskById(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));
        log.info("Received task: {}", task);
        return task;
    }

    public TaskEntity createTask(TaskDto taskDto) {
        TaskEntity task = TaskEntity.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .columnId(taskDto.getColumnId())
                .projectId(taskDto.getProjectId())
                .build();
        task.setCreateDate(dateProvider.getCurrentDate());
        TaskEntity savedTask = taskRepository.save(task);
        log.info("Saved task: {}", savedTask);
        return savedTask;
    }

    public TaskEntity updateTask(Long id, TaskDto taskDto) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));
        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setColumnId(taskDto.getColumnId());
        task.setProjectId(taskDto.getProjectId());
        taskRepository.save(task);
        log.info("Updated task: {}", task);
        return task;
    }

    public void deleteTask(Long id) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task", id));
        taskRepository.delete(task);
    }

    public TaskDto mapTask(TaskEntity task) {
        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getCreateDate(),
                task.getProjectId(),
                task.getColumnId()
        );
    }

    public static TaskShortDto mapToShortTaskResponse(TaskEntity task) {
        return new TaskShortDto(
                task.getId(),
                task.getName(),
                task.getDescription()
        );
    }
}
