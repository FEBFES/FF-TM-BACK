package com.febfes.fftmback.controller;

import com.febfes.fftmback.annotation.ApiCreate;
import com.febfes.fftmback.annotation.ApiGet;
import com.febfes.fftmback.annotation.ProtectedApi;
import com.febfes.fftmback.dto.TaskCommentDto;
import com.febfes.fftmback.service.TaskCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/projects/task")
@RequiredArgsConstructor
@ProtectedApi
@Tag(name = "TaskComment")
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    @Operation(summary = "Create new task comment")
    @ApiCreate(path = "comment")
    public TaskCommentDto createTask(
            @RequestBody TaskCommentDto taskCommentDto
    ) {
        return taskCommentService.saveTaskComment(taskCommentDto);
    }

    @Operation(summary = "Get task comments")
    @ApiGet(path = "{taskId}/comments")
    public List<TaskCommentDto> getTaskComments(
            @PathVariable Long taskId
    ) {
        return taskCommentService.getCommentsByTaskId(taskId);
    }
}
