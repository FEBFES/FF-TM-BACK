package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import com.febfes.fftmback.service.*;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static com.febfes.fftmback.integration.ColumnControllerTest.COLUMN_NAME;
import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;
import static com.febfes.fftmback.integration.ProjectControllerTest.PROJECT_NAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class TaskControllerTest extends BasicTestClass {

    public static final String TASK_NAME = "Task name";

    private Long createdProjectId;
    private Long createdColumnId;
    private String createdUsername;
    private String token;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private ProjectService projectService;

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

        ProjectEntity projectEntity = projectService.createProject(
                dtoBuilders.createProjectDto(PROJECT_NAME),
                createdUsername
        );
        createdProjectId = projectEntity.getId();

        TaskColumnEntity columnEntity = columnService.createColumn(
                createdProjectId,
                dtoBuilders.createColumnDto(COLUMN_NAME, 4)
        );
        createdColumnId = columnEntity.getId();
    }

    @Test
    void successfulGetTasksTest() {
        taskService.createTask(
                createdProjectId,
                createdColumnId,
                dtoBuilders.createTaskDto(TASK_NAME + "1"),
                createdUsername
        );

        taskService.createTask(
                createdProjectId,
                createdColumnId,
                dtoBuilders.createTaskDto(TASK_NAME + "2"),
                createdUsername
        );

        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{projectId}/columns/{columnId}/tasks".formatted(PATH_TO_PROJECTS_API), createdProjectId, createdColumnId);
        response.then()
                .statusCode(HttpStatus.SC_OK);

        int size = response
                .jsonPath()
                .getInt("data.size()");
        Assertions.assertThat(size)
                .isEqualTo(2);
    }

    @Test
    void successfulCreateOfTaskTest() {
        TaskDto taskDto = dtoBuilders.createTaskDto(TASK_NAME);

        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(TASK_NAME));
    }

    @Test
    void failedCreateOfTaskTest() {
        TaskDto taskDto = dtoBuilders.createTaskDto();

        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditOfTaskTest() {
        TaskDto createTaskDto = dtoBuilders.createTaskDto(TASK_NAME);
        Response createResponse = createNewTask(createTaskDto);
        long createdTaskId = createResponse.jsonPath().getLong("id");

        String newTaskName = TASK_NAME + "edit";
        TaskDto editTaskDto = dtoBuilders.createTaskDto(newTaskName);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editTaskDto)
                .when()
                .put("%s/{projectId}/columns/{columnId}/tasks/{taskId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId, createdTaskId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedEditOfTaskTest() {
        String wrongTaskId = "54731584";
        TaskDto createTaskDto = dtoBuilders.createTaskDto(TASK_NAME);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(createTaskDto)
                .when()
                .put("%s/{projectId}/columns/{columnId}/tasks/{taskId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId, wrongTaskId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void successfulDeleteOfTaskTest() {
        TaskDto createTaskDto = dtoBuilders.createTaskDto(TASK_NAME);
        Response createResponse = createNewTask(createTaskDto);
        long createdTaskId = createResponse.jsonPath().getLong("id");

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{projectId}/columns/{columnId}/tasks/{taskId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId, createdTaskId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedDeleteOfTaskTest() {
        String wrongTaskId = "54731584";

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{projectId}/columns/{columnId}/tasks/{taskId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId, wrongTaskId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private Response createNewTask(TaskDto taskDto) {
        return requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(taskDto)
                .when()
                .post("%s/{projectId}/columns/{columnId}/tasks".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId);
    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }
}
