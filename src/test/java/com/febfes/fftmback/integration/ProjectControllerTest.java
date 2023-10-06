package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.response.Response;
import org.apache.commons.compress.utils.Lists;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.febfes.fftmback.util.DtoBuilders.PASSWORD;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.instancio.Select.field;

class ProjectControllerTest extends BasicTestClass {

    public static final String PATH_TO_PROJECTS_API = "/api/v1/projects";

    @Test
    void successfulGetProjectsTest() {
        projectService.createProject(Instancio.create(ProjectEntity.class), createdUserId);
        projectService.createProject(Instancio.create(ProjectEntity.class), createdUserId);

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
        ProjectDto projectDto = Instancio.create(ProjectDto.class);

        Response createResponse = createNewProject(projectDto);
        createResponse.then()
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo(projectDto.name()));
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
        ProjectDto projectDto = Instancio.of(ProjectDto.class)
                .set(field(ProjectDto::name), null)
                .create();

        createNewProject(projectDto).then()
                .statusCode(HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    void successfulEditOfProjectTest() {
        Long createdProjectId = createNewProject();
        ProjectDto editProjectDto = Instancio.create(ProjectDto.class);

        ProjectDto updatedProjectDto = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(editProjectDto)
                .when()
                .put("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(ProjectDto.class);

        Assertions.assertThat(updatedProjectDto.name())
                .isEqualTo(editProjectDto.name());
        Assertions.assertThat(updatedProjectDto.description())
                .isEqualTo(editProjectDto.description());
    }

    @Test
    void failedEditOfProjectTest() {
        String wrongProjectId = "54731584";
        ProjectDto editProjectDto = Instancio.create(ProjectDto.class);

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
        Long createdProjectId = createNewProject();
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failedDeleteOfProjectTest() {
        String wrongProjectId = "54731584";
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{id}".formatted(PATH_TO_PROJECTS_API), wrongProjectId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void successfulSetProjectFavouriteTest() {
        Long createdProjectId = createNewProject();
        List<PatchDto> patchDtoList = List.of(new PatchDto("update", "isFavourite", Boolean.TRUE));

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(patchDtoList)
                .when()
                .patch("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);
        OneProjectDto updatedProject = projectService.getProjectForUser(createdProjectId, createdUserId);
        Assertions.assertThat(updatedProject.isFavourite())
                .isTrue();
        List<ProjectDto> userProjects = projectService.getProjectsForUser(createdUserId, Lists.newArrayList());
        Optional<ProjectDto> userProject = userProjects.stream().findFirst();
        Assertions.assertThat(userProject)
                .isNotEmpty();
        Assertions.assertThat(userProject.get().isFavourite())
                .isTrue();
    }

    @Test
    void failedSetProjectFavouriteTest() {
        Long createdProjectId = createNewProject();
        List<PatchDto> patchDtoList = List.of(new PatchDto("update", "isFavourite!", Boolean.TRUE));

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(patchDtoList)
                .when()
                .patch("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);
        OneProjectDto updatedProject = projectService.getProjectForUser(createdProjectId, createdUserId);
        Assertions.assertThat(updatedProject.isFavourite())
                .isFalse();
        List<ProjectDto> userProjects = projectService.getProjectsForUser(createdUserId, Lists.newArrayList());
        Optional<ProjectDto> userProject = userProjects.stream().findFirst();
        Assertions.assertThat(userProject)
                .isNotEmpty();
        Assertions.assertThat(userProject.get().isFavourite())
                .isFalse();
    }

    @Test
    void successfulUpdateNameTest() {
        String newName = "new name";
        Long createdProjectId = createNewProject();
        List<PatchDto> patchDtoList = List.of(new PatchDto("update", "name", newName));

        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(patchDtoList)
                .when()
                .patch("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);
        ProjectEntity updatedProject = projectService.getProject(createdProjectId);
        Assertions.assertThat(updatedProject.getName())
                .isEqualTo(newName);
    }

    @Test
    void successfulGetUserProjectsTest() {
        Long secondCreatedUserId = createNewUser();
        UserEntity secondUser = userService.getUserById(secondCreatedUserId);
        projectService.createProject(Instancio.create(ProjectEntity.class), secondCreatedUserId);

        String tokenForSecondUser = authenticationService.authenticateUser(
                UserEntity.builder().username(secondUser.getUsername()).encryptedPassword(PASSWORD).build()
        ).accessToken();
        Response response = given().header("Authorization", "Bearer " + tokenForSecondUser)
                .contentType(ContentType.JSON)
                .when()
                .get(PATH_TO_PROJECTS_API);
        response.then()
                .statusCode(HttpStatus.SC_OK);

        int size = response
                .jsonPath()
                .getInt("data.size()");
        Assertions.assertThat(size)
                .isEqualTo(1);
    }

    @Test
    void successfulGetOneProjectTest() {
        Long createdProjectId = createNewProject();
        Response response = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId);
        OneProjectDto oneProjectDto = response.then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(OneProjectDto.class);
        Assertions.assertThat(oneProjectDto.userRoleOnProject().name())
                .isEqualTo(RoleName.OWNER);
    }

    @Test
    void successfulGetProjectsWithSort() {
        String name = "getProjectsWithSortName";
        projectService.createProject(DtoBuilders.createProject(name + "1"), createdUserId);
        projectService.createProject(DtoBuilders.createProject(name + "2"), createdUserId);

        List<ProjectDto> projects = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get(PATH_TO_PROJECTS_API + "?sort=-id&sort=+name")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(new TypeRef<>() {
                });

        Assertions.assertThat(projects)
                .hasSize(2);
        Assertions.assertThat(projects.get(0).name())
                .isEqualTo(name + "2");
        Assertions.assertThat(projects.get(1).name())
                .isEqualTo(name + "1");
    }

    private Response createNewProject(ProjectDto projectDto) {
        return requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(projectDto)
                .when()
                .post(PATH_TO_PROJECTS_API);
    }
}
