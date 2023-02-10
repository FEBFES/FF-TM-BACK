package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
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

class ColumnControllerTest extends BasicTestClass {

    private static final String PATH_TO_PROJECTS_API = "/api/v1/projects";
    private static final String COLUMN_NAME = "Column name";
    private static final String PROJECT_NAME = "Project name";

    private Long createdProjectId;

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
    }

    @AfterEach
    void afterEach() {
        databaseCleanup.execute();
    }

    @Test
    void successfulGetColumnsTest() {
        columnService.createColumn(
                createdProjectId,
                new ColumnDto(null, COLUMN_NAME + "1", null, 1, null)
        );

        columnService.createColumn(
                createdProjectId,
                new ColumnDto(null, COLUMN_NAME + "2", null, 2, null)
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}/dashboard".formatted(PATH_TO_PROJECTS_API), createdProjectId);
        response.then()
                .statusCode(HttpStatus.SC_OK);

        int size = response
                .jsonPath()
                .getInt("columns.size()");
        Assertions.assertThat(size)
                .isEqualTo(2);
    }

    @Test
    void successfulCreateOfColumnTest() {
        ColumnDto columnDto = new ColumnDto(null, COLUMN_NAME, null, 1, null);

        createNewColumn(columnDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(COLUMN_NAME));
    }

    @Test
    void failedCreateOfColumnTest() {
        ColumnDto columnDto = new ColumnDto(null, null, null, 1, null);

        createNewColumn(columnDto)
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditOfColumnTest() {
        ColumnDto createColumnDto = new ColumnDto(null, COLUMN_NAME, null, 1, null);
        Response createResponse = createNewColumn(createColumnDto);
        long createdColumnId = createResponse.jsonPath().getLong("id");

        String newColumnName = COLUMN_NAME + "edit";
        ColumnDto editColumnDto = new ColumnDto(null, newColumnName, null, 1, null);

        given()
                .contentType(ContentType.JSON)
                .body(editColumnDto)
                .when()
                .put("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, createdColumnId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedEditOfColumnTest() {
        String wrongColumnId = "54731584";
        ColumnDto columnDto = new ColumnDto(null, COLUMN_NAME, null, 1, null);

        given()
                .contentType(ContentType.JSON)
                .body(columnDto)
                .when()
                .put("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, wrongColumnId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void successfulDeleteOfColumnTest() {
        ColumnDto columnDto = new ColumnDto(null, COLUMN_NAME, null, 1, null);
        Response createResponse = createNewColumn(columnDto);
        long createdColumnId = createResponse.jsonPath().getLong("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, createdColumnId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedDeleteOfColumnTest() {
        String wrongColumnId = "54731584";

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, wrongColumnId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private Response createNewColumn(ColumnDto columnDto) {
        return given()
                .contentType(ContentType.JSON)
                .body(columnDto)
                .when()
                .post("%s/{projectId}/columns".formatted(PATH_TO_PROJECTS_API), createdProjectId);
    }
}
