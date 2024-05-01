package com.febfes.fftmback.integration;


import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskTypeService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.service.project.ProjectManagementService;
import com.febfes.fftmback.service.project.ProjectMemberService;
import com.febfes.fftmback.util.DatabaseCleanup;
import com.febfes.fftmback.util.DtoBuilders;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.febfes.fftmback.util.DtoBuilders.PASSWORD;
import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Slf4j
public class BasicTestClass {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    protected AuthenticationService authenticationService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ColumnService columnService;

    @Autowired
    protected ProjectMemberService projectMemberService;

    @Autowired
    protected TaskTypeService taskTypeService;

    @Autowired
    @Qualifier("projectManagementService")
    protected ProjectManagementService projectManagementService;

    @LocalServerPort
    private Integer port;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    protected String token;
    protected Long createdUserId;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;

        UserEntity user = DtoBuilders.createUser();
        authenticationService.registerUser(user);
        createdUserId = userService.getUserIdByUsername(user.getUsername());
        token = authenticationService.authenticateUser(
                UserEntity.builder().username(user.getUsername()).encryptedPassword(PASSWORD).build()
        ).accessToken();
    }

    @AfterEach
    void afterEach() {
        databaseCleanup.execute();
    }

    protected RequestSpecification requestWithBearerToken() {
        return given().header("Authorization", "Bearer " + token);
    }

    protected Long createNewUser() {
        UserEntity user = DtoBuilders.createUser();
        authenticationService.registerUser(user);
        return userService.getUserIdByUsername(user.getUsername());
    }

    protected Long createNewProject() {
        Long projectId = projectManagementService.createProject(DtoBuilders.createProject(createdUserId), createdUserId)
                .getId();
        columnService.createDefaultColumnsForProject(projectId);
        taskTypeService.createDefaultTaskTypesForProject(projectId);
        projectMemberService.addUserToProjectAndChangeRole(projectId, createdUserId, RoleName.OWNER);
        return projectId;
    }
}

