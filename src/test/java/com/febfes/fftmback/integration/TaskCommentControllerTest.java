package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.dao.TaskCommentEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.TaskCommentDto;
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

        TaskCommentDto comment = TaskCommentDto.builder()
                .creatorId(userId)
                .taskId(taskId)
                .text("Some comment text")
                .build();

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(comment)
                .when()
                .post("%s/task/add-comment".formatted(PATH_TO_PROJECTS_API))
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void getTaskCommentTest() {
        Long userId = createNewUser();
        Long projectId = createNewProject();
        Long taskId = createNewTask(projectId, userId);

        TaskCommentEntity comment = taskCommentService.saveTaskComment(
                TaskCommentEntity.builder()
                    .creator(UserEntity.builder().id(userId).build())
                    .taskId(taskId)
                    .text("Some comment text")
                    .build()
        );

        Optional<TaskCommentDto> receivedCommentOpt = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/task/%s/comments".formatted(PATH_TO_PROJECTS_API, taskId.toString()))
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .jsonPath()
                .getList(".", TaskCommentDto.class)
                .stream()
                .findFirst();

        assertThat(receivedCommentOpt).isPresent();
        receivedCommentOpt.ifPresent(receivedComment -> {
            assertThat(receivedComment.id()).isEqualTo(comment.getTaskId());
            assertThat(receivedComment.taskId()).isEqualTo(comment.getTaskId());
            assertThat(receivedComment.creatorId()).isEqualTo(comment.getCreator().getId());
            assertThat(receivedComment.text()).isEqualTo(comment.getText());
        });
    }
}
