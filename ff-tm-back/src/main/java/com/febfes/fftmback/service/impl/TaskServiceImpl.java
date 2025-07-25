package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.config.jwt.User;
import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.TaskTypeEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskShortDto;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.exception.ProjectColumnException;
import com.febfes.fftmback.mapper.TaskMapper;
import com.febfes.fftmback.repository.ProjectRepository;
import com.febfes.fftmback.repository.TaskRepository;
import com.febfes.fftmback.repository.TaskViewRepository;
import com.febfes.fftmback.service.FileService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.service.order.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;
    private final TaskViewRepository taskViewRepository;
    private final ProjectRepository projectRepository;

    private final TaskTypeService taskTypeService;
    private final OrderService<TaskEntity> orderService;
    private final UserService userService;
    private final FileService fileService;

    @Override
    public List<TaskShortDto> getTasks(int page, int limit, Long columnId, TaskSpec taskSpec) {
        Pageable pageableRequest = PageRequest.of(page, limit, Sort.by(ORDER_FIELD_NAME));
        List<TaskView> tasks = taskViewRepository.findAll(taskSpec.and(byColumnId(columnId)), pageableRequest)
                .getContent();
        var userIds = tasks.stream()
                .flatMap(t -> Stream.of(t.getOwnerId(), t.getAssigneeId()))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        var users = userService.getUsers(userIds).stream()
                .collect(Collectors.toMap(UserDto::id, Function.identity()));
        log.debug(RECEIVED_TASKS_SIZE_LOG, tasks.size());
        return tasks.stream()
                .map(t -> taskMapper.taskViewToTaskShortDto(
                        t,
                        users.get(t.getOwnerId()),
                        users.get(t.getAssigneeId())
                ))
                .toList();
    }

    @Override
    public List<TaskView> getTasks(Set<Long> columnIds, TaskSpec taskSpec) {
        List<TaskView> tasks = taskViewRepository.findAll(
                taskSpec.and(columnIdIn(columnIds)),
                Sort.by(ORDER_FIELD_NAME)
        );
        log.debug(RECEIVED_TASKS_SIZE_LOG, tasks.size());
        return tasks;
    }

    @Override
    public TaskDto getTaskById(Long id) {
        var task = taskViewRepository.findById(id).orElseThrow(Exceptions.taskNotFound(id));
        List<FileEntity> files = fileService.getFilesByEntityId(id, EntityType.TASK);
        var ownerAssignee = getOwnerAndAssignee(task);
        return taskMapper.taskViewToTaskDto(task, files, ownerAssignee.getLeft(), ownerAssignee.getRight());
    }

    @Override
    public TaskShortDto getTaskShortById(Long id) {
        var task = taskViewRepository.findById(id).orElseThrow(Exceptions.taskNotFound(id));
        var ownerAssignee = getOwnerAndAssignee(task);
        return taskMapper.taskViewToTaskShortDto(task, ownerAssignee.getLeft(), ownerAssignee.getRight());
    }

    @Override
    public Long createTask(TaskEntity task, User user) {
        task.setOwnerId(user.id());
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
    public void updateTask(TaskEntity editTask, User user) {
        Long id = editTask.getId();
        TaskEntity task = taskRepository.findById(id).orElseThrow(Exceptions.taskNotFound(id));
        task.setName(editTask.getName());
        task.setDescription(editTask.getDescription());
        task.setColumnId(editTask.getColumnId());
        task.setProjectId(editTask.getProjectId());
        task.setPriority(editTask.getPriority());
        task.setAssigneeId(editTask.getAssigneeId());
        task.setDeadlineDate(editTask.getDeadlineDate());
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

    // owner = left, assignee = right
    private Pair<UserDto, UserDto> getOwnerAndAssignee(TaskView task) {
        UserDto owner = userService.getUser(task.getOwnerId());
        UserDto assignee = userService.getUser(task.getAssigneeId());
        return Pair.of(owner, assignee);
    }
}
