package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.*;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.EditTaskDto;
import com.febfes.fftmback.service.*;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static com.febfes.fftmback.integration.ColumnControllerTest.COLUMN_NAME;
import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;
import static com.febfes.fftmback.integration.ProjectControllerTest.PROJECT_NAME;
import static com.febfes.fftmback.integration.TaskControllerTest.TASK_NAME;
import static io.restassured.RestAssured.given;

class EntityOrderTest extends BasicTestClass {

    private Long createdProjectId;
    private Long createdColumnId;
    private String token;

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

    @BeforeEach
    void beforeEach() {
        authenticationService.registerUser(
                UserEntity.builder().email(USER_EMAIL).username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        );
        token = authenticationService.authenticateUser(
                UserEntity.builder().username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        ).accessToken();

        Long createdUserId = userService.getUserIdByUsername(USER_USERNAME);
        ProjectEntity projectEntity = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                createdUserId
        );
        createdProjectId = projectEntity.getId();

        TaskColumnEntity columnEntity = columnService.createColumn(TaskColumnEntity
                .builder()
                .name(COLUMN_NAME)
                .projectId(createdProjectId)
                .build()
        );
        createdColumnId = columnEntity.getId();

        for (int i = 0; i < 4; i++) {
            taskService.createTask(TaskEntity.builder()
                            .name(TASK_NAME + i)
                            .columnId(createdColumnId)
                            .projectId(createdProjectId)
                            .build(),
                    createdUserId);
        }
    }

    @Test
    void addTaskOrderTest() {  // checking whether the order of the tasks is set when creating them
        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        List<TaskView> tasks = taskService.getTasks(createdColumnId, emptyTaskSpec);
        List<Integer> orders = tasks.stream()
                .map(TaskView::getEntityOrder)
                .toList();
        Assertions.assertThat(orders)
                .isEqualTo(IntStream.range(1, tasks.size() + 1).boxed().toList());
    }

    @Test
    void changeTaskOrderTest() {
        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        List<TaskView> tasks = taskService.getTasks(createdColumnId, emptyTaskSpec);
        List<Long> idsList = tasksToIds(tasks);
        TaskView lastTask = tasks.get(tasks.size() - 1);
        idsList.remove(lastTask.getId());
        idsList.add(0, lastTask.getId());

        EditTaskDto editTaskDto = new EditTaskDto(lastTask.getName(), lastTask.getDescription(),
                null, lastTask.getPriority(), null, 1);
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editTaskDto)
                .when()
                .put("%s/{projectId}/columns/{columnId}/tasks/{taskId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId, createdColumnId, lastTask.getId())
                .then()
                .statusCode(HttpStatus.SC_OK);
        List<Long> idsListAfterUpdate = tasksToIds(taskService.getTasks(createdColumnId, emptyTaskSpec));
        Assertions.assertThat(idsListAfterUpdate)
                .isEqualTo(idsList);
    }

    @Test
    void removeTaskOrderTest() {
        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        List<TaskView> tasks = taskService.getTasks(createdColumnId, emptyTaskSpec);
        Assertions.assertThat(tasks.size())
                .isGreaterThan(1);
        TaskView taskToDelete = tasks.get(1);
        taskService.deleteTask(taskToDelete.getId());

        List<TaskView> tasksAfterDelete = taskService.getTasks(createdColumnId, emptyTaskSpec);
        List<Integer> orders = tasksAfterDelete.stream()
                .map(TaskView::getEntityOrder)
                .toList();
        Assertions.assertThat(orders)
                .isEqualTo(IntStream.range(1, tasks.size()).boxed().toList());
    }

    @Test
    void addColumnOrderTest() { // checking whether the order of the columns is set when creating them
        // The columns have already been created, since columns are created by default when creating a new project
        List<Integer> orders = getDashboard().columns()
                .stream()
                .map(ColumnWithTasksDto::order)
                .toList();
        Assertions.assertThat(orders)
                .isEqualTo(IntStream.range(1, orders.size() + 1).boxed().toList());
    }

    @Test
    void changeColumnOrder() {
        List<ColumnWithTasksDto> columns = getDashboard().columns();
        List<Long> columnIdWithOrderList = columns.stream()
                .map(ColumnWithTasksDto::id)
                .collect(Collectors.toList());
        // swap 2nd and 3rd columns
        Assertions.assertThat(columns.size())
                .isGreaterThan(2);
        ColumnWithTasksDto thirdColumn = columns.get(2);
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(DtoBuilders.createColumnDto(thirdColumn.name(), 2))
                .when()
                .put(
                        "%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId,
                        thirdColumn.id()
                )
                .then()
                .statusCode(HttpStatus.SC_OK);
        Collections.swap(columnIdWithOrderList, 1, 2);
        Assertions.assertThat(getDashboard().columns().stream().map(ColumnWithTasksDto::id).toList())
                .isEqualTo(columnIdWithOrderList);
    }

    @Test
    void removeColumnOrderTest() {
        List<ColumnWithTasksDto> columns = getDashboard().columns();
        Assertions.assertThat(columns.size())
                .isGreaterThan(1);
        Long columnIdToDelete = columns.get(1).id();
        columnService.deleteColumn(columnIdToDelete);

        List<ColumnWithTasksDto> columnsAfterDelete = getDashboard().columns();
        List<Integer> orders = columnsAfterDelete.stream()
                .map(ColumnWithTasksDto::order)
                .toList();
        Assertions.assertThat(orders)
                .isEqualTo(IntStream.range(1, columns.size()).boxed().toList());
    }

    private List<Long> tasksToIds(List<TaskView> taskList) {
        return taskList.stream()
                .map(TaskView::getId)
                .collect(Collectors.toList());
    }

    private DashboardDto getDashboard() {
        return requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/projects/{id}/dashboard", createdProjectId)
                .then()
                .extract()
                .response()
                .as(DashboardDto.class);
    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }
}
