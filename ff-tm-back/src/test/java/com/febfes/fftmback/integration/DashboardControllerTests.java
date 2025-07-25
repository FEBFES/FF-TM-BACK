package com.febfes.fftmback.integration;


import com.febfes.fftmback.config.jwt.User;
import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.integration.basic.BasicTestClass;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.project.DashboardService;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.febfes.fftmback.util.UnitTestBuilders.user;


class DashboardControllerTests extends BasicTestClass {

    @Autowired
    private TaskService taskService;

    @Autowired
    private DashboardService dashboardService;

    @Test
    void successfulGetDashboardTest() {
        Long projectId = createNewProject();
        TaskColumnEntity columnEntity = columnService.createColumn(DtoBuilders.createColumn(projectId));
        TaskEntity task = DtoBuilders.createTask(projectId, columnEntity.getId());
        taskService.createTask(task, new User(createdUserId, null, null));

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

        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        var dashboard1 = dashboardService.getDashboard(projectId, emptyTaskSpec);
        var dashboard2 = dashboardService.getDashboard(projectId2, emptyTaskSpec);
        List<Long> columnIds1 = dashboard1.columns().stream().map(ColumnWithTasksDto::id).toList();
        Long columnId4 = dashboard2.columns().get(0).id();

        createTask(projectId, columnIds1.get(0), taskName);
        createTask(projectId, columnIds1.get(0), taskName + "2");
        createTask(projectId, columnIds1.get(0), "2");
        createTask(projectId, columnIds1.get(1), taskName + "3");
        createTask(projectId, columnIds1.get(2), "3");
        createTask(projectId2, columnId4, taskName);

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

    private void createTask(Long projectId, Long columnId, String taskName) {
        taskService.createTask(
                DtoBuilders.createTask(projectId, columnId, taskName),
                user(createdUserId)
        );
    }
}
