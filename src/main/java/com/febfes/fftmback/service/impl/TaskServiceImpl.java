package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.EditTaskDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.TaskRepository;
import com.febfes.fftmback.repository.TaskViewRepository;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.febfes.fftmback.domain.common.specification.TaskSpec.byColumnId;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private static final String RECEIVED_TASKS_SIZE_LOG = "Received tasks size: {}";

    private final TaskRepository taskRepository;
    private final TaskViewRepository taskViewRepository;
    private final UserService userService;
    private final TaskTypeService taskTypeService;

    @Override
    public List<TaskView> getTasks(
            int page,
            int limit,
            Long columnId,
            TaskSpec taskSpec
    ) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        List<TaskView> tasks = taskViewRepository.findAll(taskSpec.and(byColumnId(columnId)), pageableRequest)
                .getContent();
        log.info(RECEIVED_TASKS_SIZE_LOG, tasks.size());
        return tasks;
    }

    @Override
    public List<TaskView> getTasks(
            Long columnId,
            TaskSpec taskSpec
    ) {
        List<TaskView> tasks = taskViewRepository.findAll(taskSpec.and(byColumnId(columnId)));
        log.info(RECEIVED_TASKS_SIZE_LOG, tasks.size());
        return tasks;
    }

    @Override
    public TaskView getTaskById(Long id) {
        return taskViewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TaskEntity.ENTITY_NAME, id));
    }

    @Override
    public TaskView createTask(
            TaskEntity task,
            String username
    ) {
        task.setOwnerId(userService.getUserIdByUsername(username));
        if (nonNull(task.getTaskType())) {
            fillTaskType(task, task.getTaskType().getName(), task.getProjectId());
        }
        TaskEntity savedTask = taskRepository.save(task);
        log.info("Saved task: {}", savedTask);
        return getTaskById(savedTask.getId());
    }

    @Override
    public TaskView updateTask(
            Long id,
            Long projectId,
            Long columnId,
            EditTaskDto editTaskDto
    ) {
        TaskEntity task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(TaskEntity.ENTITY_NAME, id));
        task.setName(editTaskDto.name());
        task.setDescription(editTaskDto.description());
        task.setColumnId(columnId);
        task.setProjectId(projectId);
        task.setPriority(editTaskDto.priority());
        task.setAssigneeId(editTaskDto.assigneeId());
        fillTaskType(task, editTaskDto.type(), projectId);
        taskRepository.save(task);
        log.info("Updated task: {}", task);
        return getTaskById(id);
    }

    @Override
    public void deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            log.info("Task with id={} deleted", id);
        } else {
            throw new EntityNotFoundException(TaskEntity.ENTITY_NAME, id);
        }
    }

    private void fillTaskType(TaskEntity task, String typeName, Long projectId) {
        if (isNull(typeName) || isNull(projectId)) {
            task.setTaskType(null);
            return;
        }
        Optional<TaskTypeEntity> taskTypeEntity = taskTypeService
                .getTaskTypeByNameAndProjectId(typeName, task.getProjectId());
        if (taskTypeEntity.isPresent()) {
            task.setTaskType(taskTypeEntity.get());
        } else {
            TaskTypeEntity newTaskType = taskTypeService.createTaskType(
                    TaskTypeEntity.builder()
                            .name(typeName)
                            .projectId(projectId)
                            .build()
            );
            task.setTaskType(newTaskType);
        }

    }
}
