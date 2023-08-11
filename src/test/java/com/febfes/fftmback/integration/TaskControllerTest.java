package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.common.TaskPriority;
import com.febfes.fftmback.domain.dao.*;
import com.febfes.fftmback.dto.*;
import com.febfes.fftmback.service.*;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static com.febfes.fftmback.integration.ColumnControllerTest.COLUMN_NAME;
import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;
import static com.febfes.fftmback.integration.ProjectControllerTest.PROJECT_NAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class TaskControllerTest extends BasicTestClass {

    public static final String TASK_NAME = "Task name";
    public static final String TASK_TYPE = "bugggg";

    private Long createdProjectId;
    private Long createdColumnId;
    private String createdUsername;
    private String token;

    @TempDir
    static File tempDir;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private DtoBuilders dtoBuilders;

    @Autowired
    private TaskTypeService taskTypeService;

    @Autowired
    private FileService fileService;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("files.folder",
                () -> String.format("%s\\", tempDir.getPath())
        );
    }

    @BeforeEach
    void beforeEach() {
        authenticationService.registerUser(
                UserEntity.builder().email(USER_EMAIL).username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        );
        token = authenticationService.authenticateUser(
                UserEntity.builder().username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        ).accessToken();
        createdUsername = USER_USERNAME;

        ProjectEntity projectEntity = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                createdUsername
        );
        createdProjectId = projectEntity.getId();

        TaskColumnEntity columnEntity = columnService.createColumn(TaskColumnEntity
                .builder()
                .name(COLUMN_NAME)
                .projectId(createdProjectId)
                .build()
        );
        createdColumnId = columnEntity.getId();
    }

    @Test
    void successfulGetTasksTest() {
        createNewTask(TASK_NAME + "1");
        createNewTask(TASK_NAME + "2");

        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{projectId}/columns/{columnId}/tasks".formatted(PATH_TO_PROJECTS_API), createdProjectId, createdColumnId);
        response.then()
                .statusCode(HttpStatus.SC_OK);

        int size = response
                .jsonPath()
                .getInt("data.size()");
        Assertions.assertEquals(2, size);
    }

    @Test
    void successfulGetTasksWithFilterTest() {
        createNewTask(TASK_NAME + "1");
        createNewTask(TASK_NAME + "2");

        Map<String, String> params = new HashMap<>();
        params.put("taskName", TASK_NAME);
        params.put("taskDescription", "1");
        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .params(params)
                .pathParams("projectId", createdProjectId, "columnId", createdColumnId)
                .when()
                .get("%s/{projectId}/columns/{columnId}/tasks".formatted(PATH_TO_PROJECTS_API));
        response.then()
                .statusCode(HttpStatus.SC_OK);

        int size = response
                .jsonPath()
                .getInt("data.size()");
        Assertions.assertEquals(2, size);
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
        TaskDto taskDto = TaskDto.builder().build();

        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditOfTaskTest() {
        TaskDto createTaskDto = dtoBuilders.createTaskDto(TASK_NAME);
        Response createResponse = createNewTask(createTaskDto);
        long createdTaskId = createResponse.jsonPath().getLong("id");

        authenticationService.registerUser(UserEntity.builder().email(USER_EMAIL + "1")
                .username(USER_USERNAME + "1").encryptedPassword(USER_PASSWORD).build());
        Long newUserId = userService.getUserIdByUsername(USER_USERNAME + "1");
        String newTaskName = TASK_NAME + "edit";
        EditTaskDto editTaskDto = new EditTaskDto(newTaskName, "newDescription", newUserId, TaskPriority.HIGH, "bug");

        TaskShortDto taskShortDto = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editTaskDto)
                .when()
                .put("%s/{projectId}/columns/{columnId}/tasks/{taskId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId, createdTaskId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(TaskShortDto.class);
        Assertions.assertEquals(editTaskDto.name(), taskShortDto.name());
        Assertions.assertEquals(editTaskDto.description(), taskShortDto.description());
        Assertions.assertEquals(editTaskDto.assigneeId(), taskShortDto.assignee().id());
        Assertions.assertEquals(USER_EMAIL + "1", taskShortDto.assignee().email());
        Assertions.assertEquals(USER_USERNAME + "1", taskShortDto.assignee().username());
        Assertions.assertEquals(editTaskDto.priority(), taskShortDto.priority());
        Assertions.assertEquals(editTaskDto.type(), taskShortDto.type());
        Assertions.assertNotNull(taskShortDto.createDate());
        Assertions.assertNotNull(taskShortDto.updateDate());
        Assertions.assertNotEquals(taskShortDto.createDate(), taskShortDto.updateDate());
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

    @Test
    void createTaskWithTypeTest() {
        taskTypeService.createTaskType(TaskTypeEntity
                .builder()
                .name(TASK_TYPE)
                .projectId(createdProjectId)
                .build()
        );
        TaskDto taskDto = dtoBuilders.createTaskDtoWithType(TASK_NAME, TASK_TYPE);
        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("type", equalTo(TASK_TYPE));
    }

    @Test
    void createTaskWithPriorityTest() {
        TaskDto taskDto = dtoBuilders.createTaskDtoWithPriority(TASK_NAME, TaskPriority.LOW.name().toLowerCase());
        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("priority", equalTo(TaskPriority.LOW.name()));
    }

    @Test
    void createTaskWithWrongColumnIdTest() {
        long wrongColumnId = 20L;
        TaskDto taskDto = dtoBuilders.createTaskDtoWithPriority(TASK_NAME, TaskPriority.LOW.name().toLowerCase());
        ApiErrorDto errorDto = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(taskDto)
                .when()
                .post("%s/{projectId}/columns/{columnId}/tasks".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, wrongColumnId)
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .extract()
                .response()
                .as(ApiErrorDto.class);
        Assertions.assertEquals(
                "Project with id=%d doesn't contain column with id=%d".formatted(createdProjectId, wrongColumnId),
                errorDto.message()
        );
    }

    @Test
    void saveTaskFileTest() {
        TaskView task = createNewTask(TASK_NAME);
        saveTaskFile(task.getId());
    }

    @Test
    void getTaskFilesTest() {
        TaskView task = createNewTask(TASK_NAME);
        saveTaskFile(task.getId());

        TaskView updatedTask = taskService.getTaskById(task.getId());
        Assertions.assertEquals(1, updatedTask.getFilesCounter());

        List<FileEntity> taskFiles = fileService.getFilesByEntityId(task.getId(), EntityType.TASK);
        Assertions.assertEquals(1, taskFiles.size());
    }

    @Test
    void deleteTaskFileTest() {
        TaskView task = createNewTask(TASK_NAME);

        saveTaskFile(task.getId());
        List<FileEntity> taskFiles = fileService.getFilesByEntityId(task.getId(), EntityType.TASK);
        Assertions.assertEquals(1, taskFiles.size());

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/v1/files/{fileId}", taskFiles.get(0).getId())
                .then()
                .statusCode(HttpStatus.SC_OK);

        List<FileEntity> newTaskFiles = fileService.getFilesByEntityId(task.getId(), EntityType.TASK);
        Assertions.assertEquals(0, newTaskFiles.size());
    }


    private Response createNewTask(TaskDto taskDto) {
        return requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(taskDto)
                .when()
                .post("%s/{projectId}/columns/{columnId}/tasks".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId);
    }

    private TaskView createNewTask(String taskName) {
        return taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(createdColumnId)
                        .name(taskName)
                        .description("1")
                        .build(),
                createdUsername
        );
    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }

    private void saveTaskFile(Long taskId) {
        String taskFileName = "task-file.txt";
        File taskFile = new File(String.format("src/test/resources/%s", taskFileName));

        List<TaskFileDto> savedFiles = requestWithBearerToken()
                .multiPart("files", taskFile)
                .contentType("multipart/form-data")
                .when()
                .post("/api/v1/files/task/{taskId}", taskId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(new TypeRef<>() {
                });
        Assertions.assertEquals(1, savedFiles.size());
        Assertions.assertEquals(userService.getUserIdByUsername(createdUsername), savedFiles.get(0).userId());
        Assertions.assertEquals(taskFileName, savedFiles.get(0).name());
    }
}
