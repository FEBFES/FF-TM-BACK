package com.febfes.fftmback.integration;


import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


class DashboardControllerTests extends BasicTestClass {

    @Autowired
    private ColumnService columnService;

    @Autowired
    private TaskService taskService;

    @Test
    void successfulGetDashboardTest() {
        Long projectId = createNewProject();
        TaskColumnEntity columnEntity = columnService.createColumn(DtoBuilders.createColumn(projectId));
        TaskEntity task = DtoBuilders.createTask(projectId, columnEntity.getId());
        taskService.createTask(task, createdUserId);

        DashboardDto dashboardDto = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/projects/{id}/dashboard", projectId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(DashboardDto.class);
        ColumnWithTasksDto createdColumn = dashboardDto.columns().get(4);
        Assertions.assertEquals(columnEntity.getName(), createdColumn.name());
        Assertions.assertEquals(task.getName(), createdColumn.tasks().get(0).name());
    }

    @Test
    void successfulGetDashboardWithFilterTest() {
        Long projectId = createNewProject();
        String taskName = "task_name";
        taskService.createTask(
                DtoBuilders.createTask(projectId, 1L, taskName),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTask(projectId, 1L, taskName + "2"),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTask(projectId, 1L, "2"),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTask(projectId, 2L, taskName + "3"),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTask(projectId, 3L, "3"),
                createdUserId
        );

        Long projectId2 = createNewProject();
        taskService.createTask(
                DtoBuilders.createTask(projectId2, 5L, taskName),
                createdUserId
        );

        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .params("taskName", taskName)
                .when()
                .get("/api/v1/projects/{id}/dashboard", projectId);
        DashboardDto dashboardDto = response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(DashboardDto.class);
        Assertions.assertEquals(2, dashboardDto.columns().get(0).tasks().size());
    }
}
