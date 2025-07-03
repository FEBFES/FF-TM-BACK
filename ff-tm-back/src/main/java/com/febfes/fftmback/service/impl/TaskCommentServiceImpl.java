package com.febfes.fftmback.service.impl;

import com.febfes.fftmback.dto.TaskCommentDto;
import com.febfes.fftmback.mapper.TaskCommentMapper;
import com.febfes.fftmback.repository.TaskCommentRepository;
import com.febfes.fftmback.service.TaskCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskCommentMapper taskCommentMapper;

    @Override
    public TaskCommentDto saveTaskComment(TaskCommentDto taskComment) {
        taskCommentRepository.save(taskCommentMapper.taskCommentToTaskCommentEntity(taskComment));
        log.info("Saved task comment: {}", taskComment);
        return taskCommentMapper.projectionToDto(
                taskCommentRepository.findOneWithCreatorNameById(taskComment.id())
        );
    }

    @Override
    public List<TaskCommentDto> getCommentsByTaskId(Long taskId) {
        return taskCommentMapper.projectionListToDtoList(
                taskCommentRepository.findCommentsWithCreatorNameByTaskId(taskId)
        );
    }
}
