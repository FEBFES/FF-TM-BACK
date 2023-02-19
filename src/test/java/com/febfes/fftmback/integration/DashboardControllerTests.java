package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
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
import static org.hamcrest.Matchers.equalTo;


class DashboardControllerTests extends BasicTestClass {

    private static final String PROJECT_NAME = "Project name";
    private static final String COLUMN_NAME = "Column name";
    private static final String TASK_NAME = "Task name";

    private String createdUsername;
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

    @Autowired
    private DtoBuilders dtoBuilders;

    @BeforeEach
    void beforeEach() {
        token = authenticationService.registerUser(
                new UserDetailsDto(USER_EMAIL, USER_USERNAME, USER_PASSWORD)
        ).token();
        createdUsername = userService.loadUserByUsername(USER_USERNAME).getUsername();
    }

    @Test
    void testSingleSuccessTest1() {
        ProjectEntity projectEntity = projectService.createProject(
                dtoBuilders.createProjectDto(PROJECT_NAME),
                createdUsername
        );
        TaskColumnEntity columnEntity = columnService.createColumn(
                projectEntity.getId(),
                dtoBuilders.createColumnDto(COLUMN_NAME)
        );
        taskService.createTask(
                projectEntity.getId(),
                columnEntity.getId(),
                dtoBuilders.createTaskDto(TASK_NAME),
                createdUsername
        );

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/projects/{id}/dashboard", projectEntity.getId())
                .then()
                .statusCode(200)
                .body("columns[4].name", equalTo(COLUMN_NAME))
                .body("columns[4].tasks[0].name", equalTo(TASK_NAME));
    }

    @Test
    void successfulDashboardWithFilterTest() {
        ProjectEntity projectEntity = projectService.createProject(
                dtoBuilders.createProjectDto(PROJECT_NAME),
                createdUsername
        );
        taskService.createTask(
                projectEntity.getId(),
                1L,
                dtoBuilders.createTaskDto(TASK_NAME),
                createdUsername
        );
        taskService.createTask(
                projectEntity.getId(),
                1L,
                dtoBuilders.createTaskDto(TASK_NAME + "2"),
                createdUsername
        );

        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .params("taskFilter", "[{\"property\":\"name\",\"operator\":\"EQUAL\",\"value\":\"%s\"}]".formatted(TASK_NAME))
                .when()
                .get("/api/v1/projects/{id}/dashboard", projectEntity.getId());
        DashboardDto dashboardDto = response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(DashboardDto.class);
        Assertions.assertEquals(1, dashboardDto.columns().get(0).tasks().size());
    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }
}
