package com.febfes.fftmback.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.febfes.fftmback.domain.common.query.FilterRequest;
import com.febfes.fftmback.domain.common.query.FilterSpecification;
import com.febfes.fftmback.domain.common.query.Operator;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.repository.TaskRepository;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.util.DateUtils;
import com.febfes.fftmback.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Override
    public List<TaskEntity> getTasks(
            int page,
            int limit,
            Long columnId,
            String filter
    ) {
        Pageable pageableRequest = PageRequest.of(page, limit);
        List<TaskEntity> tasks = taskRepository.findAll(makeTasksFilter(columnId, filter), pageableRequest).getContent();
        log.info("Received tasks size: {}", tasks.size());
        return tasks;
    }

    @Override
    public List<TaskEntity> getTasks(
            Long columnId,
            String filter
    ) {
        List<TaskEntity> tasks = taskRepository.findAll(makeTasksFilter(columnId, filter));
        log.info("Received tasks size: {}", tasks.size());
        return tasks;
    }

    @Override
    public List<TaskEntity> getTasks(String filter) {
        List<TaskEntity> tasks = taskRepository.findAll(makeTasksFilter(filter));
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
                .createDate(DateUtils.getCurrentDate())
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

    private FilterSpecification<TaskEntity> makeTasksFilter(String filter) {
        if (nonNull(filter)) {
            log.info("Receiving tasks with a filter: {}", filter);
            List<FilterRequest> filters = JsonUtils.convertStringToObject(filter, new TypeReference<>() {
            });
            return new FilterSpecification<>(filters);
        }
        return new FilterSpecification<>(new ArrayList<>());
    }

    private FilterSpecification<TaskEntity> makeTasksFilter(
            Long columnId,
            String filter
    ) {
        List<FilterRequest> filters = new ArrayList<>();
        filters.add(FilterRequest.builder()
                .property("columnId")
                .operator(Operator.EQUAL)
                .value(columnId)
                .build());
        if (nonNull(filter)) {
            log.info("Receiving tasks with a filter: {}", filter);
            List<FilterRequest> additionalFilters = JsonUtils.convertStringToObject(filter, new TypeReference<>() {
            });
            filters.addAll(additionalFilters);
        }
        return new FilterSpecification<>(filters);
    }
}
