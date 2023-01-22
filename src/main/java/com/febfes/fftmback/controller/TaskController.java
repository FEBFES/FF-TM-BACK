package com.febfes.fftmback.controller;

import com.febfes.fftmback.domain.TaskEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.service.TaskService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final @NonNull TaskService taskService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<TaskDto> getTasks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit
    ) {

        List<TaskEntity> tasks = taskService.getTasks(page, limit);
        return tasks.stream()
                .map(taskService::mapTask)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TaskDto getTaskById(@PathVariable Long id) {
        TaskEntity task = taskService.getTaskById(id);
        return taskService.mapTask(task);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        TaskEntity task = taskService.createTask(taskDto);
        return taskService.mapTask(task);
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public TaskDto updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        TaskEntity task = taskService.updateTask(id, taskDto);
        return taskService.mapTask(task);
    }

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public void deleteAd(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
