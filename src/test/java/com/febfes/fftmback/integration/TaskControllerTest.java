package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.common.TaskPriority;
import com.febfes.fftmback.domain.dao.*;
import com.febfes.fftmback.dto.EditTaskDto;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.dto.TaskFileDto;
import com.febfes.fftmback.dto.TaskShortDto;
import com.febfes.fftmback.dto.error.ErrorDto;
import com.febfes.fftmback.service.FileService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;
import static org.hamcrest.Matchers.equalTo;
import static org.instancio.Select.field;

class TaskControllerTest extends BasicTestClass {

    private Long createdProjectId;
    private Long createdColumnId;

    @TempDir
    static File tempDir;

    @Autowired
    private TaskService taskService;

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
        createdProjectId = createNewProject();
        createdColumnId = columnService.createColumn(DtoBuilders.createColumn(createdProjectId)).getId();
    }

    @Test
    void successfulGetTasksTest() {
        createNewTask();
        createNewTask();

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
        String name = "getTasksWithFilterTestName";
        String description = "getTasksWithFilterTestDesc";
        createNewTask(name, description);
        createNewTask(name, description);

        Map<String, String> params = new HashMap<>();
        params.put("taskName", name);
        params.put("taskDescription", description);
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
    void successfulCreateTaskTest() {
        TaskDto taskDto = Instancio.of(TaskDto.class)
                .set(field(TaskDto::deadlineDate), LocalDateTime.now().plusDays(1L))
                .create();

        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(taskDto.name()));
    }

    @Test
    void failedCreateTaskTest() {
        TaskDto emptyTask = TaskDto.builder().build();

        createNewTask(emptyTask)
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditTaskTest() {
        TaskView task = createNewTask();
        Long newUserId = createNewUser();
        UserEntity newUser = userService.getUserById(newUserId);
        EditTaskDto editTaskDto = Instancio.of(EditTaskDto.class)
                .set(field(EditTaskDto::assigneeId), newUserId)
                .set(field(EditTaskDto::deadlineDate), LocalDateTime.now().plusDays(1L))
                .create();

        TaskShortDto taskShortDto = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editTaskDto)
                .when()
                .put("%s/{projectId}/columns/{columnId}/tasks/{taskId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId, task.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(TaskShortDto.class);
        Assertions.assertEquals(editTaskDto.name(), taskShortDto.name());
        Assertions.assertEquals(editTaskDto.description(), taskShortDto.description());
        Assertions.assertEquals(editTaskDto.assigneeId(), taskShortDto.assignee().id());
        Assertions.assertEquals(newUser.getEmail(), taskShortDto.assignee().email());
        Assertions.assertEquals(newUser.getUsername(), taskShortDto.assignee().username());
        Assertions.assertEquals(editTaskDto.priority(), taskShortDto.priority());
        Assertions.assertEquals(editTaskDto.type(), taskShortDto.type());
        Assertions.assertNotNull(taskShortDto.createDate());
        Assertions.assertNotNull(taskShortDto.updateDate());
        Assertions.assertNotEquals(taskShortDto.createDate(), taskShortDto.updateDate());
        Assertions.assertEquals(editTaskDto.order(), taskShortDto.order());
    }

    @Test
    void failedEditOfTaskTest() {
        String wrongTaskId = "54731584";
        TaskDto createTaskDto = Instancio.of(TaskDto.class)
                .set(field(TaskDto::deadlineDate), null)
                .create();

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
        long createdTaskId = createNewTask().getId();

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
        TaskTypeEntity taskType = DtoBuilders.createTaskType(createdProjectId);
        taskTypeService.createTaskType(taskType);
        TaskDto taskDto = Instancio.of(TaskDto.class)
                .set(field(TaskDto::type), taskType.getName())
                .set(field(TaskDto::deadlineDate), null)
                .create();
        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("type", equalTo(taskType.getName()));
    }

    @Test
    void createTaskWithPriorityTest() {
        TaskDto taskDto = Instancio.of(TaskDto.class)
                .set(field(TaskDto::priority), TaskPriority.LOW)
                .set(field(TaskDto::deadlineDate), null)
                .create();
        createNewTask(taskDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("priority", equalTo(taskDto.priority().name()));
    }

    @Test
    void createTaskWithWrongColumnIdTest() {
        long wrongColumnId = 20L;
        TaskDto taskDto = Instancio.of(TaskDto.class)
                .set(field(TaskDto::deadlineDate), null)
                .create();
        ErrorDto errorDto = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(taskDto)
                .when()
                .post("%s/{projectId}/columns/{columnId}/tasks".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, wrongColumnId)
                .then()
                .statusCode(HttpStatus.SC_CONFLICT)
                .extract()
                .response()
                .as(ErrorDto.class);
        Assertions.assertEquals(
                "Project with id=%d doesn't contain column with id=%d".formatted(createdProjectId, wrongColumnId),
                errorDto.message()
        );
    }

    @Test
    void saveTaskFileTest() {
        TaskView task = createNewTask();
        saveTaskFile(task.getId());
    }

    @Test
    void getTaskFilesTest() {
        TaskView task = createNewTask();
        saveTaskFile(task.getId());

        TaskView updatedTask = taskService.getTaskById(task.getId());
        Assertions.assertEquals(1, updatedTask.getFilesCounter());

        List<FileEntity> taskFiles = fileService.getFilesByEntityId(task.getId(), EntityType.TASK);
        Assertions.assertEquals(1, taskFiles.size());
    }

    @Test
    void deleteTaskFileTest() {
        TaskView task = createNewTask();

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

    private TaskView createNewTask() {
        Long taskId = taskService.createTask(DtoBuilders.createTask(createdProjectId, createdColumnId), createdUserId);
        return taskService.getTaskById(taskId);
    }

    private void createNewTask(String name, String description) {
        TaskEntity task = DtoBuilders.createTask(createdProjectId, createdColumnId, name);
        task.setDescription(description);
        taskService.createTask(task, createdUserId);
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
        Assertions.assertEquals(createdUserId, savedFiles.get(0).userId());
        Assertions.assertEquals(taskFileName, savedFiles.get(0).name());
    }
}
