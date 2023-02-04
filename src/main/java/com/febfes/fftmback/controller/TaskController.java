package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.domain.TaskEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.mapper.TaskMapper;
import com.febfes.fftmback.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@Tag(name = "Task")
public class TaskController {

    private final @NonNull TaskService taskService;

    @Operation(summary = "Get tasks with pagination")
    @ApiGet(path = "{projectId}/columns/{columnId}/tasks")
    public List<TaskDto> getTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @PathVariable Long projectId,
            @PathVariable Long columnId
    ) {

        List<TaskEntity> tasks = taskService.getTasks(page, limit, columnId);
        return tasks.stream()
                .map(TaskMapper.INSTANCE::taskToTaskDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get task by its id")
    @ApiGetOne(path = "{projectId}/columns/{columnId}/tasks/{id}")
    @SuppressWarnings("MVCPathVariableInspection") // fake warn "Cannot resolve path variable 'id' in @RequestMapping"
    public TaskDto getTaskById(@PathVariable Long projectId, @PathVariable Long columnId, @PathVariable Long id) {
        TaskEntity task = taskService.getTaskById(id);
        return TaskMapper.INSTANCE.taskToTaskDto(task);
    }

    @Operation(summary = "Create new task")
    @ApiCreate(path = "{projectId}/columns/{columnId}/tasks")
    public TaskDto createTask(@PathVariable Long projectId, @PathVariable Long columnId, @RequestBody TaskDto taskDto) {
        TaskEntity task = taskService.createTask(projectId, columnId, taskDto);
        return TaskMapper.INSTANCE.taskToTaskDto(task);
    }

    @Operation(summary = "Edit task by its id")
    @ApiEdit(path = "{projectId}/columns/{columnId}/tasks/{id}")
    public TaskDto updateTask(
            @PathVariable Long projectId,
            @PathVariable Long columnId,
            @PathVariable Long id,
            @RequestBody TaskDto taskDto
    ) {
        TaskEntity task = taskService.updateTask(id, projectId, columnId, taskDto);
        return TaskMapper.INSTANCE.taskToTaskDto(task);
    }

    @Operation(summary = "Delete task by its id")
    @ApiDelete(path = "{projectId}/columns/{columnId}/tasks/{id}")
    public void deleteTask(@PathVariable Long projectId, @PathVariable Long columnId, @PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
