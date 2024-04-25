package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.DashboardDto;
import com.febfes.fftmback.dto.OneProjectDto;
import com.febfes.fftmback.dto.PatchDto;
import com.febfes.fftmback.dto.ProjectDto;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.project.DashboardService;
import com.febfes.fftmback.service.project.ProjectManagementService;
import com.febfes.fftmback.util.DtoBuilders;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.http.ContentType;
import io.restassured.mapper.TypeRef;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
import org.apache.commons.compress.utils.Lists;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static com.febfes.fftmback.service.impl.ColumnServiceImpl.DEFAULT_COLUMNS;
import static com.febfes.fftmback.util.DtoBuilders.PASSWORD;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.instancio.Select.field;

@Slf4j
class ProjectControllerTest extends BasicTestClass {

    public static final String PATH_TO_PROJECTS_API = "/api/v1/projects";

    @Autowired
    TaskService taskService;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    @Qualifier("projectManagementServiceDecorator")
    protected ProjectManagementService projectManagementServiceDecorator;

    @Test
    void successfulGetProjectsTest() {
        projectManagementServiceDecorator.createProject(Instancio.create(ProjectEntity.class), createdUserId);
        projectManagementServiceDecorator.createProject(Instancio.create(ProjectEntity.class), createdUserId);

        waitPools();

        List<ProjectDto> projects = projectMemberService.getProjectsForUser(createdUserId, new ArrayList<>());
        Assertions.assertThat(projects.size())
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

        waitPools();

        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        DashboardDto dashboard = dashboardService.getDashboard(createdProjectId, emptyTaskSpec);
        Assertions.assertThat(dashboard.columns().size())
                .isEqualTo(DEFAULT_COLUMNS.size());
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
        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        Long columnId = dashboardService.getDashboard(createdProjectId, emptyTaskSpec).columns().get(0).id();
        Long taskId = taskService.createTask(DtoBuilders.createTask(createdProjectId, columnId), createdUserId);
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{id}".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK);
        Assertions.assertThatThrownBy(() -> projectManagementServiceDecorator.getProject(createdProjectId))
                .isInstanceOf(EntityNotFoundException.class);
        Assertions.assertThat(columnService.getOrderedColumns(createdProjectId))
                .isEmpty();
        Assertions.assertThatThrownBy(() -> taskService.getTaskById(taskId))
                .isInstanceOf(EntityNotFoundException.class);
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
        OneProjectDto updatedProject = projectMemberService.getProjectForUser(createdProjectId, createdUserId);
        Assertions.assertThat(updatedProject.isFavourite())
                .isTrue();
        List<ProjectDto> userProjects = projectMemberService.getProjectsForUser(createdUserId, Lists.newArrayList());
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
        OneProjectDto updatedProject = projectMemberService.getProjectForUser(createdProjectId, createdUserId);
        Assertions.assertThat(updatedProject.isFavourite())
                .isFalse();
        List<ProjectDto> userProjects = projectMemberService.getProjectsForUser(createdUserId, Lists.newArrayList());
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
        ProjectEntity updatedProject = projectManagementServiceDecorator.getProject(createdProjectId);
        Assertions.assertThat(updatedProject.getName())
                .isEqualTo(newName);
    }

    @Test
    void successfulGetUserProjectsTest() {
        Long secondCreatedUserId = createNewUser();
        UserEntity secondUser = userService.getUserById(secondCreatedUserId);
        projectManagementServiceDecorator.createProject(Instancio.create(ProjectEntity.class), secondCreatedUserId);

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
        String name1 = "getProjectsWithSortName1";
        String name2 = "getProjectsWithSortName2";
        projectManagementServiceDecorator.createProject(DtoBuilders.createProject(name1), createdUserId);
        projectManagementServiceDecorator.createProject(DtoBuilders.createProject(name2), createdUserId);

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
                .isEqualTo(name2);
        Assertions.assertThat(projects.get(1).name())
                .isEqualTo(name1);
    }

    private Response createNewProject(ProjectDto projectDto) {
        return requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(projectDto)
                .when()
                .post(PATH_TO_PROJECTS_API);
    }

    @SneakyThrows
    private void waitPools() {
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.awaitTermination(1, TimeUnit.MINUTES);
        log.info(commonPool.getRunningThreadCount() + " " + commonPool);
//        try {
//            commonPool.inv
//        } catch (InterruptedException E) {
//            fail("Can't await for pools to finish");
//        }
//        try {
//            ForkJoinPool.commonPool().wait();
//            System.out.println("Thread in runnable state");
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        if (!ForkJoinPool.commonPool().awaitTermination(1, TimeUnit.MINUTES)) {
//            log.error("Pools aren't finished");
//            fail("Pools aren't finished");
//        }
//        ForkJoinPool.commonPool().awaitTermination(5, TimeUnit.SECONDS);
    }
}
