package com.febfes.fftmback.service;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;

import java.util.List;

public interface TaskCommentService {

    TaskCommentEntity saveTaskComment(TaskCommentEntity taskComment);

    List<TaskCommentEntity> getCommentsByTaskId(Long taskId);
}
