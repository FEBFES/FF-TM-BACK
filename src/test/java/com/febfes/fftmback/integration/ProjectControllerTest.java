package com.febfes.fftmback.integration;

import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.util.DatabaseCleanup;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

class ProjectControllerTest extends BasicTestClass {

    private static final String PATH_TO_PROJECTS_API = "/api/v1/projects";
    private static final String PROJECT_NAME = "Project name";
    private static final String PROJECT_DESCRIPTION = "Project description";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @AfterEach
    void afterEach() {
        databaseCleanup.execute();
    }

    @Test
    void successfulGetProjectsTest() {
        projectService.createProject(
                new ProjectDto.Builder(PROJECT_NAME + "1").build()
        );
        projectService.createProject(
                new ProjectDto.Builder(PROJECT_NAME + "2").build()
        );

        Response response = given()
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
        ProjectDto projectDto = new ProjectDto.Builder(PROJECT_NAME).build();

        Response createResponse = createNewProject(projectDto);
        createResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(PROJECT_NAME));
        Long createdProjectId = createResponse.jsonPath().getLong("id");

        // 4 default columns
        Response dashboardResponse = given()
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
        ProjectDto projectDto = new ProjectDto.Builder(null).description(PROJECT_DESCRIPTION).build();

        createNewProject(projectDto).then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditOfProjectTest() {
        ProjectDto createProjectDto = new ProjectDto.Builder(PROJECT_NAME).build();
        Response createResponse = createNewProject(createProjectDto);
        Long createdProjectId = createResponse.jsonPath().getLong("id");

        String newProjectName = PROJECT_NAME + "edit";
        ProjectDto editProjectDto = new ProjectDto.Builder(newProjectName).build();

        given()
                .contentType(ContentType.JSON)
                .body(editProjectDto)
                .when()
                .put("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);

        Response getResponse = given()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId);
        Assertions.assertThat(getResponse.jsonPath().getString("name"))
                .isEqualTo(newProjectName);
    }

    @Test
    void failedEditOfProjectTest() {
        String wrongProjectId = "54731584";
        ProjectDto editProjectDto = new ProjectDto.Builder(PROJECT_NAME).build();

        given()
                .contentType(ContentType.JSON)
                .body(editProjectDto)
                .when()
                .put("%s/{id}".formatted(PATH_TO_PROJECTS_API), wrongProjectId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void successfulDeleteOfProjectTest() {
        ProjectDto createProjectDto = new ProjectDto.Builder(PROJECT_NAME).build();
        Response createResponse = createNewProject(createProjectDto);
        Long createdProjectId = createResponse.jsonPath().getLong("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedDeleteOfProjectTest() {
        String wrongProjectId1 = "54731584";

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{id}".formatted(PATH_TO_PROJECTS_API), wrongProjectId1)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    private Response createNewProject(ProjectDto projectDto) {
        return given()
                .contentType(ContentType.JSON)
                .body(projectDto)
                .when()
                .post(PATH_TO_PROJECTS_API);
    }
}
