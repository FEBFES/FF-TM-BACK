package com.febfes.fftmback.integration;


import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.service.*;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static io.restassured.RestAssured.given;


class DashboardControllerTests extends BasicTestClass {

    private static final String PROJECT_NAME = "Project name";
    private static final String COLUMN_NAME = "Column name";
    private static final String TASK_NAME = "Task name";

    private Long createdUserId;
    private String token;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @BeforeEach
    void beforeEach() {
        authenticationService.registerUser(
                UserEntity.builder().email(USER_EMAIL).username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        );
        token = authenticationService.authenticateUser(
                UserEntity.builder().username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        ).accessToken();
        createdUserId = userService.getUserIdByUsername(USER_USERNAME);
    }

    @Test
    void successfulGetDashboardTest() {
        ProjectEntity projectEntity = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                createdUserId
        );
        TaskColumnEntity columnEntity = columnService.createColumn(TaskColumnEntity
                        .builder()
                        .name(COLUMN_NAME)
                        .projectId(projectEntity.getId())
                        .build(),
                createdUserId
        );
        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(projectEntity.getId())
                        .columnId(columnEntity.getId())
                        .name(TASK_NAME)
                        .build(),
                createdUserId
        );

        DashboardDto dashboardDto = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/projects/{id}/dashboard", projectEntity.getId())
                .then()
                .statusCode(200)
                .extract()
                .response()
                .as(DashboardDto.class);
        ColumnWithTasksDto createdColumn = dashboardDto.columns().get(4);
        Assertions.assertEquals(COLUMN_NAME, createdColumn.name());
        Assertions.assertEquals(TASK_NAME, createdColumn.tasks().get(0).name());
    }

    @Test
    void successfulGetDashboardWithFilterTest() {
        ProjectEntity projectEntity = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                createdUserId
        );
        taskService.createTask(
                TaskEntity.builder().projectId(projectEntity.getId()).columnId(1L).name(TASK_NAME).build(),
                createdUserId
        );
        taskService.createTask(
                TaskEntity.builder().projectId(projectEntity.getId()).columnId(1L).name(TASK_NAME + "2").build(),
                createdUserId
        );
        taskService.createTask(
                TaskEntity.builder().projectId(projectEntity.getId()).columnId(1L).name("2").build(),
                createdUserId
        );
        taskService.createTask(
                TaskEntity.builder().projectId(projectEntity.getId()).columnId(2L).name(TASK_NAME + "3").build(),
                createdUserId
        );
        taskService.createTask(
                TaskEntity.builder().projectId(projectEntity.getId()).columnId(3L).name("3").build(),
                createdUserId
        );

        ProjectEntity projectEntity2 = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME + "2").build(),
                createdUserId
        );
        taskService.createTask(
                TaskEntity.builder().projectId(projectEntity2.getId()).columnId(5L).name(TASK_NAME).build(),
                createdUserId
        );

        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .params("taskName", TASK_NAME)
                .when()
                .get("/api/v1/projects/{id}/dashboard", projectEntity.getId());
        DashboardDto dashboardDto = response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(DashboardDto.class);
        Assertions.assertEquals(2, dashboardDto.columns().get(0).tasks().size());
    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }
}
