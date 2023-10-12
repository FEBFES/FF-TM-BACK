package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.LongConsumer;

import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;

class ColumnControllerTest extends BasicTestClass {

    private Long createdProjectId;

    @BeforeEach
    void beforeEach() {
        ProjectEntity projectEntity = projectService.createProject(Instancio.create(ProjectEntity.class), createdUserId);
        createdProjectId = projectEntity.getId();
    }

    @Test
    void successfulGetColumnsTest() {
        Response beforeResponse = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}/dashboard".formatted(PATH_TO_PROJECTS_API), createdProjectId);
        beforeResponse.then()
                .statusCode(HttpStatus.SC_OK);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}/dashboard".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void successfulCreateColumnTest() {
        ColumnDto columnDto = Instancio.create(ColumnDto.class);

        ColumnDto createdColumn = createNewColumn(columnDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(ColumnDto.class);
        Assertions.assertThat(createdColumn.name())
                .isEqualTo(columnDto.name());
    }

    @Test
    void failedCreateColumnTest() {
        ColumnDto emptyColumn = ColumnDto.builder().build();

        createNewColumn(emptyColumn)
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditColumnTest() {
        ColumnDto createColumnDto = Instancio.create(ColumnDto.class);
        Response createResponse = createNewColumn(createColumnDto);
        long createdColumnId = createResponse.jsonPath().getLong("id");

        ColumnDto editColumnDto = Instancio.create(ColumnDto.class);

        ColumnDto editedColumnDto = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editColumnDto)
                .when()
                .put("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, createdColumnId)
                .then()
                .statusCode(HttpStatus.SC_OK).extract()
                .response()
                .as(ColumnDto.class);
        Assertions.assertThat(editedColumnDto.name())
                .isEqualTo(editColumnDto.name());
    }

    @Test
    void failedEditColumnTest() {
        String wrongColumnId = "54731584";
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(Instancio.create(ColumnDto.class))
                .when()
                .put("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, wrongColumnId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void successfulDeleteColumnTest() {
        ColumnDto columnDto = Instancio.create(ColumnDto.class);
        Response createResponse = createNewColumn(columnDto);
        long createdColumnId = createResponse.jsonPath().getLong("id");

        List<ColumnWithTasksDto> columnsBeforeDelete = getDashboard().columns();
        LongConsumer deleteFoo = (columnId) -> requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, columnId)
                .then()
                .statusCode(HttpStatus.SC_OK);
        deleteFoo.accept(createdColumnId);

        List<ColumnWithTasksDto> columnsAfterDelete = getDashboard().columns();
        Assertions.assertThat(columnsBeforeDelete.size() - 1)
                .isEqualTo(columnsAfterDelete.size());
        if (columnsAfterDelete.size() > 0) {
            ColumnWithTasksDto firstColumn = columnsAfterDelete.get(0);
            deleteFoo.accept(firstColumn.id());
            List<ColumnWithTasksDto> columnsAfterSecondDelete = getDashboard().columns();
            Assertions.assertThat(columnsAfterDelete.size() - 1)
                    .isEqualTo(columnsAfterSecondDelete.size());
            for (int i = 0; i < columnsAfterSecondDelete.size(); i++) {
                Assertions.assertThat(i + 1)
                        .isEqualTo(columnsAfterSecondDelete.get(i).order());
            }
        }
    }

    @Test
    void failedDeleteColumnTest() {
        String wrongColumnId = "54731584";
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, wrongColumnId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private Response createNewColumn(ColumnDto columnDto) {
        return requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(columnDto)
                .when()
                .post("%s/{projectId}/columns".formatted(PATH_TO_PROJECTS_API), createdProjectId);
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
