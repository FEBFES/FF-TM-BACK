package com.febfes.fftmback.integration;


import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.service.*;
import com.febfes.fftmback.util.DtoBuilders;
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
                        .build()
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
        Long projectId = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                createdUserId
        ).getId();
        taskService.createTask(
                DtoBuilders.createTaskEntity(projectId, 1L, TASK_NAME),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTaskEntity(projectId, 1L, TASK_NAME + "2"),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTaskEntity(projectId, 1L, "2"),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTaskEntity(projectId, 2L, TASK_NAME + "3"),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTaskEntity(projectId, 3L, "3"),
                createdUserId
        );

        Long projectId2 = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME + "2").build(),
                createdUserId
        ).getId();
        taskService.createTask(
                DtoBuilders.createTaskEntity(projectId2, 5L, TASK_NAME),
                createdUserId
        );

        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .params("taskName", TASK_NAME)
                .when()
                .get("/api/v1/projects/{id}/dashboard", projectId);
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
