package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.util.DatabaseCleanup;
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

    @BeforeEach
    void beforeEach() {
        ProjectEntity projectEntity = projectService.createProject(
                new ProjectDto(null, PROJECT_NAME, null, null, null)
        );
        createdProjectId = projectEntity.getId();

        TaskColumnEntity columnEntity = columnService.createColumn(
                createdProjectId,
                new ColumnDto(null, COLUMN_NAME, null, 1, null)
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
                new TaskDto(null, TASK_NAME + "1", null, null, null, null, null)
        );

        taskService.createTask(
                createdProjectId,
                createdColumnId,
                new TaskDto(null, TASK_NAME + "2", null, null, null, null, null)
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
        TaskDto taskDto = new TaskDto(null, TASK_NAME, null, null, null, null, null);

        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(TASK_NAME));
    }

    @Test
    void failedCreateOfTaskTest() {
        TaskDto taskDto = new TaskDto(null, null, null, null, null, null, null);

        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditOfTaskTest() {
        TaskDto createTaskDto = new TaskDto(null, TASK_NAME, null, null, null, null, null);
        Response createResponse = createNewTask(createTaskDto);
        long createdTaskId = createResponse.jsonPath().getLong("id");

        String newTaskName = TASK_NAME + "edit";
        TaskDto editTaskDto = new TaskDto(null, newTaskName, null, null, null, null, null);

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
        TaskDto createTaskDto = new TaskDto(null, TASK_NAME, null, null, null, null, null);

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
        TaskDto createTaskDto = new TaskDto(null, TASK_NAME, null, null, null, null, null);
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
