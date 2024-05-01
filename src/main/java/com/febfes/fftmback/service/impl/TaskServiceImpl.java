package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.exception.ProjectColumnException;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.repository.TaskRepository;
import com.febfes.fftmback.repository.TaskViewRepository;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.service.order.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.febfes.fftmback.domain.common.specification.TaskSpec.byColumnId;
import static com.febfes.fftmback.domain.common.specification.TaskSpec.columnIdIn;
import static com.febfes.fftmback.service.order.OrderServiceImpl.ORDER_FIELD_NAME;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final String RECEIVED_TASKS_SIZE_LOG = "Received {} tasks";

    private final TaskRepository taskRepository;
    private final TaskViewRepository taskViewRepository;
    private final TaskTypeService taskTypeService;
    private final ProjectRepository projectRepository;
    private final OrderService<TaskEntity> orderService;

    @Override
    public List<TaskView> getTasks(
            int page,
            int limit,
            Long columnId,
            TaskSpec taskSpec
    ) {
        Pageable pageableRequest = PageRequest.of(page, limit, Sort.by(ORDER_FIELD_NAME));
        List<TaskView> tasks = taskViewRepository.findAll(taskSpec.and(byColumnId(columnId)), pageableRequest)
                .getContent();
        log.info(RECEIVED_TASKS_SIZE_LOG, tasks.size());
        return tasks;
    }

    @Override
    public List<TaskView> getTasks(
            Set<Long> columnIds,
            TaskSpec taskSpec
    ) {
        List<TaskView> tasks = taskViewRepository.findAll(
                taskSpec.and(columnIdIn(columnIds)),
                Sort.by(ORDER_FIELD_NAME)
        );
        log.info(RECEIVED_TASKS_SIZE_LOG, tasks.size());
        return tasks;
    }

    @Override
    public TaskView getTaskById(Long id) {
        return taskViewRepository.findById(id).orElseThrow(Exceptions.taskNotFound(id));
    }

    @Override
    public Long createTask(
            TaskEntity task,
            Long userId
    ) {
        task.setOwnerId(userId);
        Long projectId = task.getProjectId();
        Long columnId = task.getColumnId();
        if (nonNull(task.getTaskType())) {
            fillTaskType(task, task.getTaskType().getName(), projectId);
        }
        if (!projectRepository.doesProjectEntityContainColumn(projectId, columnId)) {
            throw new ProjectColumnException(projectId, columnId);
        }
        task.setEntityOrder(orderService.getNewOrder(task));
        TaskEntity savedTask = taskRepository.save(task);
        log.info("Saved task {} with name = {}", savedTask.getId(), savedTask.getName());
        return savedTask.getId();
    }

    @Override
    public void updateTask(TaskEntity editTask) {
        Long id = editTask.getId();
        TaskEntity task = taskRepository.findById(id).orElseThrow(Exceptions.taskNotFound(id));
        task.setName(editTask.getName());
        task.setDescription(editTask.getDescription());
        task.setColumnId(editTask.getColumnId());
        task.setProjectId(editTask.getProjectId());
        task.setPriority(editTask.getPriority());
        task.setAssigneeId(editTask.getAssigneeId());
        fillTaskType(task, editTask.getTaskType().getName(), editTask.getProjectId());
        taskRepository.save(task);
        orderService.editOrder(task, editTask.getEntityOrder());
        log.info("Updated task: {}", task);
    }

    @Override
    public void deleteTask(Long id) {
        TaskEntity task = taskRepository.findById(id).orElseThrow(Exceptions.taskNotFound(id));
        taskRepository.deleteById(id);
        orderService.removeEntity(task);
        log.info("Task with id={} deleted", id);
    }

    private void fillTaskType(TaskEntity task, String typeName, Long projectId) {
        if (isNull(typeName) || isNull(projectId)) {
            task.setTaskType(null);
            return;
        }

        task.setTaskType(taskTypeService.getTaskTypeByNameAndProjectId(typeName, task.getProjectId())
                .orElseGet(() -> taskTypeService.createTaskType(
                        TaskTypeEntity.builder()
                                .name(typeName)
                                .projectId(projectId)
                                .build()
                )));
    }
}
