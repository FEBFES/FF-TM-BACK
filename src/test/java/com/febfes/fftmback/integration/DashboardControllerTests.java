package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import com.febfes.fftmback.service.*;
import com.febfes.fftmback.util.DtoBuilders;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


class DashboardControllerTests extends BasicTestClass {

    private static final String PROJECT_NAME = "Project name";
    private static final String COLUMN_NAME = "Column name";
    private static final Integer COLUMN_ORDER = 4;
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
                dtoBuilders.createColumnDto(COLUMN_NAME, COLUMN_ORDER)
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
        //TODO Возможно стоит добавить json парсер и проверять подругому ответ

    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }
}
