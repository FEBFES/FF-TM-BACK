package com.example.fftmback.service;

import com.example.fftmback.domain.TaskEntity;
import com.example.fftmback.dto.TaskDto;
import com.example.fftmback.filter.TaskFilter;
import com.example.fftmback.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    // TODO: решить как сделать фильтр
    public List<TaskDto> getTasks(TaskFilter filter) {
        return taskRepository.findAll().stream()
                .map(this::mapBoard)
                .collect(Collectors.toList());
    }

    private TaskDto mapBoard(TaskEntity task) {
        return new TaskDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getDateIn()
        );
    }
}
