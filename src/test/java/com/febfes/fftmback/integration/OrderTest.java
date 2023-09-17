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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static com.febfes.fftmback.integration.ColumnControllerTest.COLUMN_NAME;
import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;
import static com.febfes.fftmback.integration.ProjectControllerTest.PROJECT_NAME;
import static com.febfes.fftmback.integration.TaskControllerTest.TASK_NAME;
import static io.restassured.RestAssured.given;

class OrderTest extends BasicTestClass {

    private Long createdProjectId;
    private Long createdColumnId;
    private Long createdUserId;
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

        createdUserId = userService.getUserIdByUsername(USER_USERNAME);
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
    }

    @Test
    void addTaskOrderTest() {
        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        for (int i = 0; i < 4; i++) {
            taskService.createTask(TaskEntity.builder()
                            .name(TASK_NAME + i)
                            .columnId(createdColumnId)
                            .projectId(createdProjectId)
                            .build(),
                    createdUserId);
        }
        List<TaskView> tasks = taskService.getTasks(createdColumnId, emptyTaskSpec);
        Assertions.assertThat(4)
                .isEqualTo(tasks.size());
        List<Integer> orders = tasks.stream()
                .map(TaskView::getEntityOrder)
                .toList();
        Assertions.assertThat(Arrays.asList(1, 2, 3, 4))
                .isEqualTo(orders);
    }

    @Test
    void changeTaskOrderTest() {
        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        for (int i = 0; i < 4; i++) {
            taskService.createTask(TaskEntity.builder()
                            .name(TASK_NAME + i)
                            .columnId(createdColumnId)
                            .projectId(createdProjectId)
                            .build(),
                    createdUserId);
        }
        List<TaskView> tasks = taskService.getTasks(createdColumnId, emptyTaskSpec);
        Assertions.assertThat(4)
                .isEqualTo(tasks.size());
        Function<List<TaskView>, List<Long>> tasksToIdsFoo = (taskList) -> taskList.stream()
                .map(TaskView::getId)
                .collect(Collectors.toList());
        List<Long> idsList = tasksToIdsFoo.apply(tasks);
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
        List<Long> idsListAfterUpdate = tasksToIdsFoo.apply(taskService.getTasks(createdColumnId, emptyTaskSpec));
        Assertions.assertThat(idsList)
                .isEqualTo(idsListAfterUpdate);
    }

    @Test
    void removeTaskOrderTest() {
        addTaskOrderTest();
        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        List<TaskView> tasks = taskService.getTasks(createdColumnId, emptyTaskSpec);
        Assertions.assertThat(4)
                .isEqualTo(tasks.size());
        TaskView taskToDelete = tasks.get(1);
        taskService.deleteTask(taskToDelete.getId());

        List<TaskView> tasksAfterDelete = taskService.getTasks(createdColumnId, emptyTaskSpec);
        List<Integer> orders = tasksAfterDelete.stream()
                .map(TaskView::getEntityOrder)
                .toList();
        Assertions.assertThat(Arrays.asList(1, 2, 3))
                .isEqualTo(orders);
    }

    @Test
    void addColumnOrderTest() {
        DashboardDto dashboardDto = getDashboard();
        Assertions.assertThat(dashboardDto.columns().size())
                .isEqualTo(5);
        List<Integer> orders = getDashboard().columns()
                .stream()
                .map(ColumnWithTasksDto::order)
                .toList();
        Assertions.assertThat(orders)
                .isEqualTo(Arrays.asList(1, 2, 3, 4, 5));
    }

    @Test
    void changeColumnOrder() {
        DashboardDto dashboardDto = getDashboard();
        Assertions.assertThat(dashboardDto.columns().size())
                .isEqualTo(5);
        List<Long> columnIdWithOrderList = dashboardDto.columns()
                .stream()
                .map(ColumnWithTasksDto::id)
                .collect(Collectors.toList());
        // swap 2nd and 3rd columns
        ColumnWithTasksDto thirdColumn = dashboardDto.columns().get(2);
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
        Assertions.assertThat(columnIdWithOrderList)
                .isEqualTo(getDashboard().columns().stream().map(ColumnWithTasksDto::id).toList());
    }

    @Test
    void removeColumnOrderTest() {
        addColumnOrderTest();
        List<ColumnWithTasksDto> columns = getDashboard().columns();
        Assertions.assertThat(columns.size())
                .isEqualTo(5);
        Long columnIdToDelete = columns.get(1).id();
        columnService.deleteColumn(columnIdToDelete);

        List<ColumnWithTasksDto> columnsAfterDelete = getDashboard().columns();
        List<Integer> orders = columnsAfterDelete.stream()
                .map(ColumnWithTasksDto::order)
                .toList();
        Assertions.assertThat(Arrays.asList(1, 2, 3, 4))
                .isEqualTo(orders);
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
