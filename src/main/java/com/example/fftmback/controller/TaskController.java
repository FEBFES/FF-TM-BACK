package com.example.fftmback.controller;

import com.example.fftmback.dto.TaskDto;
import com.example.fftmback.filter.TaskFilter;
import com.example.fftmback.service.TaskService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final @NonNull TaskService taskService;

    @GetMapping
    public List<TaskDto> getTasks(TaskFilter filter) {
        return taskService.getTasks(filter);
    }
}
