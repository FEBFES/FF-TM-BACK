package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.domain.TaskColumnEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.dto.TaskDto;
import com.febfes.fftmback.service.Implementation.ColumnServiceImp;
import com.febfes.fftmback.service.Implementation.ProjectServiceImp;
import com.febfes.fftmback.service.Implementation.TaskServiceImp;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class DashboardControllerTests extends BasicTestClass {

    private static final String PROJECT_NAME = "Project name";
    private static final String COLUMN_NAME = "Column name";
    private static final Integer COLUMN_ORDER = 1;
    private static final String TASK_NAME = "Task name";

    @Autowired
    private ProjectServiceImp projectServiceImp;

    @Autowired
    private ColumnServiceImp columnServiceImp;

    @Autowired
    private TaskServiceImp taskServiceImp;

    @Test
    void testSingleSuccessTest1() {

        ProjectEntity projectEntity = projectServiceImp.createProject(
                new ProjectDto(null, PROJECT_NAME, null, null)
        );
        TaskColumnEntity columnEntity = columnServiceImp.createColumn(
                projectEntity.getId(),
                new ColumnDto(null, COLUMN_NAME, null, null, COLUMN_ORDER, null)
        );
        taskServiceImp.createTask(
                projectEntity.getId(),
                columnEntity.getId(),
                new TaskDto(null, TASK_NAME, null, null, null, null)
        );

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/projects/{id}/dashboard", projectEntity.getId())
                .then()
                .statusCode(200)
                .body("name", equalTo(PROJECT_NAME))
                .body("columns[0].name", equalTo(COLUMN_NAME))
                .body("columns[0].tasks[0].name", equalTo(TASK_NAME));
        //TODO Возможно стоит добавить json парсер и проверять подругому ответ

    }

}
