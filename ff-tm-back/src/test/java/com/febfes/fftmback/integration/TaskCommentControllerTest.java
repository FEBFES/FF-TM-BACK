package com.febfes.fftmback.integration;

import com.febfes.fftmback.dto.TaskCommentDto;
import com.febfes.fftmback.integration.basic.BasicTestClass;
import com.febfes.fftmback.service.TaskCommentService;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;
import static org.assertj.core.api.Assertions.assertThat;

class TaskCommentControllerTest extends BasicTestClass {

    @Autowired
    private TaskCommentService taskCommentService;

    @Test
    void createTaskCommentTest() {
        Long userId = createNewUser();
        Long projectId = createNewProject();
        Long taskId = createNewTask(projectId, userId);

        com.febfes.fftmback.dto.TaskCommentDto comment = com.febfes.fftmback.dto.TaskCommentDto.builder()
                .creatorId(userId)
                .taskId(taskId)
                .text("Some comment text")
                .build();

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(comment)
                .when()
                .post("%s/task/comment".formatted(PATH_TO_PROJECTS_API))
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void getTaskCommentTest() {
        Long userId = createNewUser();
        Long projectId = createNewProject();
        Long taskId = createNewTask(projectId, userId);

        var comment = taskCommentService.saveTaskComment(
                TaskCommentDto.builder()
                        .creatorId(userId)
                        .taskId(taskId)
                        .text("Some comment text")
                        .build()
        );

        Optional<com.febfes.fftmback.dto.TaskCommentDto> receivedCommentOpt = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/task/%s/comments".formatted(PATH_TO_PROJECTS_API, taskId.toString()))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList(".", com.febfes.fftmback.dto.TaskCommentDto.class)
                .stream()
                .findFirst();

        assertThat(receivedCommentOpt).isPresent();
        receivedCommentOpt.ifPresent(receivedComment -> {
            assertThat(receivedComment.id()).isEqualTo(comment.taskId());
            assertThat(receivedComment.taskId()).isEqualTo(comment.taskId());
            assertThat(receivedComment.creatorId()).isEqualTo(comment.creatorId());
            assertThat(receivedComment.text()).isEqualTo(comment.text());
        });
    }
}
