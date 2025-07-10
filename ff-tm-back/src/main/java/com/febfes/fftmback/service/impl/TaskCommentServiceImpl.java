package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;
import com.febfes.fftmback.dto.TaskCommentDto;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.exception.Exceptions;
import com.febfes.fftmback.mapper.TaskCommentMapper;
import com.febfes.fftmback.repository.TaskCommentRepository;
import com.febfes.fftmback.service.TaskCommentService;
import com.febfes.fftmback.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskCommentMapper taskCommentMapper;

    private final UserService userService;

    @Override
    public TaskCommentDto saveTaskComment(TaskCommentDto taskComment) {
        taskCommentRepository.save(taskCommentMapper.taskCommentToTaskCommentEntity(taskComment));
        log.info("Saved task comment: {}", taskComment);
        var taskCommentEntity = taskCommentRepository.findById(taskComment.id())
                .orElseThrow(Exceptions.taskCommentNotFound(taskComment.id()));
        var user = userService.getUser(taskCommentEntity.getCreatorId());
        return taskCommentMapper.mapToDto(taskCommentEntity, user);
    }

    @Override
    public List<TaskCommentDto> getCommentsByTaskId(Long taskId) {
        var taskComments = taskCommentRepository.findCommentsByTaskId(taskId);
        Map<Long, UserDto> users = userService.getUsers(
                taskComments.stream().map(TaskCommentEntity::getCreatorId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(UserDto::id, Function.identity()));
        return taskComments.stream()
                .map(t -> taskCommentMapper.mapToDto(t, users.get(t.getCreatorId())))
                .toList();
    }
}
