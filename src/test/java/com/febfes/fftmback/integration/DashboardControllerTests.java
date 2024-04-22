package com.febfes.fftmback.integration;


import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.project.DashboardService;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.mock;


class DashboardControllerTests extends BasicTestClass {

    @Autowired
    private ColumnService columnService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DashboardService dashboardService;

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
        var createdColumn = dashboardDto.columns().stream()
                .filter(column -> columnEntity.getName().equals(column.name()))
                .findFirst();
        Assertions.assertTrue(createdColumn.isPresent());
        Assertions.assertEquals(task.getName(), createdColumn.get().tasks().get(0).name());
    }

    @Test
    void successfulGetDashboardWithFilterTest() {
        Long projectId = createNewProject();
        Long projectId2 = createNewProject();
        String taskName = "task_name";

        TaskSpec taskSpec = mock(TaskSpec.class);
        Long columnId1;
        Long columnId2;
        Long columnId3;
        Long columnId4;
        while (true) {
            var dashboard1 = dashboardService.getDashboard(projectId, taskSpec);
            var dashboard2 = dashboardService.getDashboard(projectId2, taskSpec);
            if (!dashboard1.columns().isEmpty() && !dashboard2.columns().isEmpty()) {
                columnId1 = dashboard1.columns().get(0).id();
                columnId2 = dashboard1.columns().get(1).id();
                columnId3 = dashboard1.columns().get(2).id();
                columnId4 = dashboard2.columns().get(0).id();
                break;
            }
        }

        taskService.createTask(
                DtoBuilders.createTask(projectId, columnId1, taskName),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTask(projectId, columnId1, taskName + "2"),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTask(projectId, columnId1, "2"),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTask(projectId, columnId2, taskName + "3"),
                createdUserId
        );
        taskService.createTask(
                DtoBuilders.createTask(projectId, columnId3, "3"),
                createdUserId
        );

        taskService.createTask(
                DtoBuilders.createTask(projectId2, columnId4, taskName),
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
