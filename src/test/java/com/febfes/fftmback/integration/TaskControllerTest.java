package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.util.DatabaseCleanup;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class TaskControllerTest extends BasicTestClass {

    private static final String PATH_TO_PROJECTS_API = "/api/v1/projects";
    private static final String TASK_NAME = "Task name";
    private static final String COLUMN_NAME = "Column name";
    private static final String PROJECT_NAME = "Project name";

    private Long createdProjectId;
    private Long createdColumnId;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private DtoBuilders dtoBuilders;

    @BeforeEach
    void beforeEach() {
        ProjectEntity projectEntity = projectService.createProject(
                dtoBuilders.createProjectDto(PROJECT_NAME)
        );
        createdProjectId = projectEntity.getId();

        TaskColumnEntity columnEntity = columnService.createColumn(
                createdProjectId,
                dtoBuilders.createColumnDto(COLUMN_NAME, 4)
        );
        createdColumnId = columnEntity.getId();
    }

    @AfterEach
    void afterEach() {
        databaseCleanup.execute();
    }

    @Test
    void successfulGetTasksTest() {
        taskService.createTask(
                createdProjectId,
                createdColumnId,
                dtoBuilders.createTaskDto(TASK_NAME + "1")
        );

        taskService.createTask(
                createdProjectId,
                createdColumnId,
                dtoBuilders.createTaskDto(TASK_NAME + "2")
        );

        Response response = given()
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

        given()
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

        given()
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

        given()
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

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{projectId}/columns/{columnId}/tasks/{taskId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId, wrongTaskId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private Response createNewTask(TaskDto taskDto) {
        return given()
                .contentType(ContentType.JSON)
                .body(taskDto)
                .when()
                .post("%s/{projectId}/columns/{columnId}/tasks".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId);
    }
}
