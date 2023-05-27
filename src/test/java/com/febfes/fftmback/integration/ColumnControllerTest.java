package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;
import static com.febfes.fftmback.integration.ProjectControllerTest.PROJECT_NAME;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class ColumnControllerTest extends BasicTestClass {

    public static final String COLUMN_NAME = "Column name";

    private Long createdProjectId;
    private String token;

    @Autowired
    private ColumnService columnService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private DtoBuilders dtoBuilders;

    @BeforeEach
    void beforeEach() {
        authenticationService.registerUser(
                UserEntity.builder().email(USER_EMAIL).username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        );
        token = authenticationService.authenticateUser(
                UserEntity.builder().username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        ).accessToken();

        ProjectEntity projectEntity = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                USER_USERNAME
        );
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

        int beforeSize = beforeResponse
                .jsonPath()
                .getInt("columns.size()");


        columnService.createColumn(TaskColumnEntity
                .builder()
                .name(COLUMN_NAME + "1")
                .projectId(createdProjectId)
                .build()
        );

        columnService.createColumn(TaskColumnEntity
                .builder()
                .name(COLUMN_NAME + "2")
                .projectId(createdProjectId)
                .build()
        );

        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}/dashboard".formatted(PATH_TO_PROJECTS_API), createdProjectId);
        response.then()
                .statusCode(HttpStatus.SC_OK);

        int size = response
                .jsonPath()
                .getInt("columns.size()");
        Assertions.assertThat(size)
                .isEqualTo(beforeSize + 2);
    }

    @Test
    void successfulCreateOfColumnTest() {
        ColumnDto columnDto = dtoBuilders.createColumnDto(COLUMN_NAME);

        createNewColumn(columnDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(COLUMN_NAME));
    }

    @Test
    void failedCreateOfColumnTest() {
        ColumnDto columnDto = dtoBuilders.createColumnDto();

        createNewColumn(columnDto)
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditOfColumnTest() {
        ColumnDto createColumnDto = dtoBuilders.createColumnDto(COLUMN_NAME);
        Response createResponse = createNewColumn(createColumnDto);
        long createdColumnId = createResponse.jsonPath().getLong("id");

        String newColumnName = COLUMN_NAME + "edit";
        ColumnDto editColumnDto = dtoBuilders.createColumnDto(newColumnName);

        requestWithBearerToken()
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
        ColumnDto columnDto = dtoBuilders.createColumnDto(COLUMN_NAME);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(columnDto)
                .when()
                .put("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, wrongColumnId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void successfulDeleteOfColumnTest() {
        ColumnDto columnDto = dtoBuilders.createColumnDto(COLUMN_NAME);
        Response createResponse = createNewColumn(columnDto);
        long createdColumnId = createResponse.jsonPath().getLong("id");

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, createdColumnId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedDeleteOfColumnTest() {
        String wrongColumnId = "54731584";

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, wrongColumnId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void changeColumnOrder() {
        DashboardDto dashboardDto = getDashboard();
        if (dashboardDto.columns().size() < 3) {
            for (int i = 1; i < 4; i++) {
                createNewColumn(dtoBuilders.createColumnDto(COLUMN_NAME + i));
            }
            dashboardDto = getDashboard();
        }
        List<Long> columnIdWithOrderList = dashboardDto
                .columns()
                .stream()
                .map(ColumnWithTasksDto::id)
                .collect(Collectors.toList());
        //swap 2 and 3 columns
        ColumnWithTasksDto thirdColumn = dashboardDto.columns().get(2);
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(dtoBuilders.createColumnDto(thirdColumn.name(), columnIdWithOrderList.get(1)))
                .when()
                .put(
                        "%s/{projectId}/columns/{columnId}".formatted(PATH_TO_PROJECTS_API),
                        createdProjectId,
                        thirdColumn.id()
                )
                .then()
                .statusCode(HttpStatus.SC_OK);
        Collections.swap(columnIdWithOrderList, 1, 2);
        dashboardDto = getDashboard();
        Assertions.assertThat(dashboardDto.columns().stream().map(ColumnWithTasksDto::id).collect(Collectors.toList()))
                .isEqualTo(columnIdWithOrderList);
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

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }
}
