package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.service.AuthenticationService;
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

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class ProjectControllerTest extends BasicTestClass {

    public static final String PATH_TO_PROJECTS_API = "/api/v1/projects";
    public static final String PROJECT_NAME = "Project name";
    public static final String PROJECT_DESCRIPTION = "Project description";

    private String createdUsername;
    private String token;

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
        createdUsername = USER_USERNAME;
    }

    @Test
    void successfulGetProjectsTest() {
        projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME + "1").build(),
                createdUsername
        );
        projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME + "2").build(),
                createdUsername
        );

        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get(PATH_TO_PROJECTS_API);
        response.then()
                .statusCode(HttpStatus.SC_OK);

        int size = response
                .jsonPath()
                .getInt("data.size()");
        Assertions.assertThat(size)
                .isEqualTo(2);
    }

    @Test
    void successfulCreateOfProjectTest() {
        ProjectDto projectDto = dtoBuilders.createProjectDto(PROJECT_NAME);

        Response createResponse = createNewProject(projectDto);
        createResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(PROJECT_NAME));
        Long createdProjectId = createResponse.jsonPath().getLong("id");

        // 4 default columns
        Response dashboardResponse = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}/dashboard".formatted(PATH_TO_PROJECTS_API), createdProjectId);
        dashboardResponse.then()
                .statusCode(HttpStatus.SC_OK);
        int size = dashboardResponse
                .jsonPath()
                .getInt("columns.size()");
        Assertions.assertThat(size)
                .isEqualTo(4);
    }

    @Test
    void failedCreateOfProjectTest() {
        ProjectDto projectDto = dtoBuilders.createProjectDto(null, PROJECT_DESCRIPTION);

        createNewProject(projectDto).then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditOfProjectTest() {
        ProjectDto createProjectDto = dtoBuilders.createProjectDto(PROJECT_NAME);
        Response createResponse = createNewProject(createProjectDto);
        Long createdProjectId = createResponse.jsonPath().getLong("id");

        String newProjectName = PROJECT_NAME + "edit";
        ProjectDto editProjectDto = dtoBuilders.createProjectDto(newProjectName);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editProjectDto)
                .when()
                .put("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);

        Response getResponse = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId);
        Assertions.assertThat(getResponse.jsonPath().getString("name"))
                .isEqualTo(newProjectName);
    }

    @Test
    void failedEditOfProjectTest() {
        String wrongProjectId = "54731584";
        ProjectDto editProjectDto = dtoBuilders.createProjectDto(PROJECT_NAME);

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editProjectDto)
                .when()
                .put("%s/{id}".formatted(PATH_TO_PROJECTS_API), wrongProjectId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void successfulDeleteOfProjectTest() {
        ProjectDto createProjectDto = dtoBuilders.createProjectDto(PROJECT_NAME);
        Response createResponse = createNewProject(createProjectDto);
        Long createdProjectId = createResponse.jsonPath().getLong("id");

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedDeleteOfProjectTest() {
        String wrongProjectId1 = "54731584";

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{id}".formatted(PATH_TO_PROJECTS_API), wrongProjectId1)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private Response createNewProject(ProjectDto projectDto) {
        return requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(projectDto)
                .when()
                .post(PATH_TO_PROJECTS_API);
    }

    private RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }
}
