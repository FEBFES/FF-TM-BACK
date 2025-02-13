package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;
import com.febfes.fftmback.repository.TaskCommentRepository;
import com.febfes.fftmback.service.TaskCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;

    @Override
    public TaskCommentEntity saveTaskComment(TaskCommentEntity taskComment) {
        return taskCommentRepository.save(taskComment);
    }

    @Override
    public List<TaskCommentEntity> getCommentsByTaskId(Long taskId) {
        return taskCommentRepository.findAll(
                Example.of(TaskCommentEntity.builder().taskId(taskId).build()),
                Sort.by(Sort.Direction.ASC, "createDate")
        );
    }
}
