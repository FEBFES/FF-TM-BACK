package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.ProjectEntity;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.auth.UserDetailsDto;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
    private UserService userService;

    @Autowired
    private DtoBuilders dtoBuilders;

    @BeforeEach
    void beforeEach() {
        token = authenticationService.registerUser(
                new UserDetailsDto(USER_EMAIL, USER_USERNAME, USER_PASSWORD)
        ).token();
        String createdUsername = userService.loadUserByUsername(USER_USERNAME).getUsername();

        ProjectEntity projectEntity = projectService.createProject(
                dtoBuilders.createProjectDto(PROJECT_NAME),
                createdUsername
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

        columnService.createColumn(
                createdProjectId,
                dtoBuilders.createColumnDto(COLUMN_NAME + "1", 4)
        );

        columnService.createColumn(
                createdProjectId,
                dtoBuilders.createColumnDto(COLUMN_NAME + "1", 5)
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
        ColumnDto columnDto = dtoBuilders.createColumnDto(COLUMN_NAME, 4);

        createNewColumn(columnDto)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(COLUMN_NAME));
    }

    @Test
    void failedCreateOfColumnTest() {
        ColumnDto columnDto = dtoBuilders.createColumnDto(4);

        createNewColumn(columnDto)
                .then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditOfColumnTest() {
        ColumnDto createColumnDto = dtoBuilders.createColumnDto(COLUMN_NAME, 4);
        Response createResponse = createNewColumn(createColumnDto);
        long createdColumnId = createResponse.jsonPath().getLong("id");

        String newColumnName = COLUMN_NAME + "edit";
        ColumnDto editColumnDto = dtoBuilders.createColumnDto(newColumnName, 5);

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
        ColumnDto columnDto = dtoBuilders.createColumnDto(COLUMN_NAME, 4);

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
        ColumnDto columnDto = dtoBuilders.createColumnDto(COLUMN_NAME, 4);
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

    private Response createNewColumn(ColumnDto columnDto) {
        return requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(columnDto)
                .when()
                .post("%s/{projectId}/columns".formatted(PATH_TO_PROJECTS_API), createdProjectId);
    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }
}
