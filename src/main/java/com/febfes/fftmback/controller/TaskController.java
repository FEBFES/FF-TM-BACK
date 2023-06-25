package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.*;
import com.febfes.fftmback.config.auth.RoleCheckerComponent;
import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.EditTaskDto;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskShortDto;
import com.febfes.fftmback.dto.parameter.ColumnParameters;
import com.febfes.fftmback.dto.parameter.TaskParameters;
import com.febfes.fftmback.mapper.TaskMapper;
import com.febfes.fftmback.service.FileService;
import com.febfes.fftmback.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/projects")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "Task")
public class TaskController {

    private final @NonNull TaskService taskService;
    private final @NonNull FileService fileService;
    private final @NonNull RoleCheckerComponent roleCheckerComponent;

    @Operation(summary = "Get tasks with pagination")
    @ApiGet(path = "{projectId}/columns/{columnId}/tasks")
    public List<TaskShortDto> getTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "filter", required = false) @FilterParam String filter,
            @ParameterObject ColumnParameters pathVars
    ) {
        List<TaskView> tasks = taskService.getTasks(page, limit, pathVars.columnId(), filter);
        return tasks.stream()
                .map(TaskMapper.INSTANCE::taskViewToTaskShortDto)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get task by its id")
    @ApiGetOne(path = "{projectId}/columns/{columnId}/tasks/{taskId}")
    public TaskDto getTaskById(@ParameterObject TaskParameters pathVars) {
        TaskView task = taskService.getTaskById(pathVars.taskId());
        List<FileEntity> files = fileService.getFilesByEntityId(pathVars.taskId(), EntityType.TASK);
        return TaskMapper.INSTANCE.taskViewToTaskDto(task, files);
    }

    @Operation(summary = "Create new task")
    @ApiCreate(path = "{projectId}/columns/{columnId}/tasks")
    public TaskShortDto createTask(
            @ParameterObject ColumnParameters pathVars,
            @RequestBody @Valid EditTaskDto taskDto
    ) {
        roleCheckerComponent.checkIfHasRole(pathVars.projectId(), RoleName.MEMBER);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        TaskView task = taskService.createTask(
                TaskMapper.INSTANCE.taskDtoToTask(pathVars.projectId(), pathVars.columnId(), taskDto),
                authentication.getName()
        );
        return TaskMapper.INSTANCE.taskViewToTaskShortDto(task);
    }

    @Operation(summary = "Edit task by its id")
    @ApiEdit(path = "{projectId}/columns/{columnId}/tasks/{taskId}")
    public TaskShortDto updateTask(
            @ParameterObject TaskParameters pathVars,
            @RequestBody EditTaskDto editTaskDto
    ) {
        roleCheckerComponent.checkIfHasRole(pathVars.projectId(), RoleName.MEMBER);
        TaskView task = taskService.updateTask(pathVars.taskId(), pathVars.projectId(), pathVars.columnId(), editTaskDto);
        return TaskMapper.INSTANCE.taskViewToTaskShortDto(task);
    }

    @Operation(summary = "Delete task by its id")
    @ApiDelete(path = "{projectId}/columns/{columnId}/tasks/{taskId}")
    public void deleteTask(@ParameterObject TaskParameters pathVars) {
        roleCheckerComponent.checkIfHasRole(pathVars.projectId(), RoleName.MEMBER);
        taskService.deleteTask(pathVars.taskId());
    }
}
