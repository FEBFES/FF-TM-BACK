package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskView;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.EditTaskDto;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;

class EntityOrderTest extends BasicTestClass {

    private Long createdProjectId;
    private Long createdColumnId;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ColumnService columnService;

    @BeforeEach
    void beforeEach() {
        createdProjectId = projectService.createProject(Instancio.create(ProjectEntity.class), createdUserId).getId();
        createdColumnId = columnService.createColumn(DtoBuilders.createColumn(createdProjectId)).getId();

        for (int i = 0; i < 4; i++) {
            taskService.createTask(DtoBuilders.createTask(createdProjectId, createdColumnId), createdUserId);
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

        EditTaskDto editTaskDto = DtoBuilders.createEditTaskDto(1);
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
        Assertions.assertThat(tasks)
                .hasSizeGreaterThan(1);
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
        Assertions.assertThat(columns)
                .hasSizeGreaterThan(2);
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
        Assertions.assertThat(columns)
                .hasSizeGreaterThan(1);
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
}
