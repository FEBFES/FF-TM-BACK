package com.febfes.fftmback.service;

import com.febfes.fftmback.dto.TaskCommentDto;

import java.util.List;

public interface TaskCommentService {

    TaskCommentDto saveTaskComment(TaskCommentDto taskComment);

    List<TaskCommentDto> getCommentsByTaskId(Long taskId);
}
