package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.config.jwt.User;
import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.EditTaskDto;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskShortDto;
import com.febfes.fftmback.dto.parameter.ColumnParameters;
import com.febfes.fftmback.dto.parameter.TaskParameters;
import com.febfes.fftmback.mapper.TaskMapper;
import com.febfes.fftmback.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Task")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @Operation(summary = "Get tasks with pagination")
    @ApiGet(path = "{projectId}/columns/{columnId}/tasks")
    public List<TaskShortDto> getTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            TaskSpec taskSpec,
            @ParameterObject ColumnParameters pathVars
    ) {
        return taskService.getTasks(page, limit, pathVars.columnId(), taskSpec);
    }

    @Operation(summary = "Get task by its id")
    @ApiGetOne(path = "{projectId}/columns/{columnId}/tasks/{taskId}")
    public TaskDto getTaskById(@ParameterObject TaskParameters pathVars) {
        return taskService.getTaskById(pathVars.taskId());
    }

    @Operation(summary = "Create new task")
    @ApiCreate(path = "{projectId}/columns/{columnId}/tasks")
    @PreAuthorize("hasAuthority(T(com.febfes.fftmback.domain.RoleName).MEMBER.name())")
    public TaskShortDto createTask(
            @AuthenticationPrincipal User user,
            @ParameterObject ColumnParameters pathVars,
            @RequestBody @Valid EditTaskDto editTaskDto
    ) {
        Long createdTaskId = taskService.createTask(
                taskMapper.taskDtoToTask(pathVars.projectId(), pathVars.columnId(), editTaskDto),
                user
        );
        return taskService.getTaskShortById(createdTaskId);
    }

    @Operation(summary = "Edit task by its id")
    @ApiEdit(path = "{projectId}/columns/{columnId}/tasks/{taskId}")
    @PreAuthorize("hasAuthority(T(com.febfes.fftmback.domain.RoleName).MEMBER.name())")
    public TaskShortDto updateTask(
            @AuthenticationPrincipal User user,
            @ParameterObject TaskParameters pathVars,
            @RequestBody @Valid EditTaskDto editTaskDto
    ) {
        TaskEntity editTask = taskMapper.taskDtoToTask(pathVars.projectId(), pathVars.columnId(), editTaskDto);
        editTask.setId(pathVars.taskId());
        taskService.updateTask(editTask, user);
        return taskService.getTaskShortById(pathVars.taskId());
    }

    @Operation(summary = "Delete task by its id")
    @ApiDelete(path = "{projectId}/columns/{columnId}/tasks/{taskId}")
    @PreAuthorize("hasAuthority(T(com.febfes.fftmback.domain.RoleName).MEMBER.name())")
    public void deleteTask(@ParameterObject TaskParameters pathVars) {
        taskService.deleteTask(pathVars.taskId());
    }
}
