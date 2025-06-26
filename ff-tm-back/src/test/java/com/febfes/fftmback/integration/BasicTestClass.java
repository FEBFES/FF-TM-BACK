package com.febfes.fftmback.integration;


import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.domain.dao.abstracts.BaseEntity;
import com.febfes.fftmback.service.*;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
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
@ImportAutoConfiguration(exclude = { KafkaAutoConfiguration.class })
class BasicTestClass {

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

    @Autowired
    protected TaskService taskService;

    @LocalServerPort
    private Integer port;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @Value("${custom-headers.user-role}")
    private String userRoleHeader;

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
        return given().header("Authorization", "Bearer " + token)
                .header(userRoleHeader, RoleName.OWNER.name());
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

    protected Long createNewTask(Long projectId, Long userId) {
        Long columnId = columnService.getOrderedColumns(projectId)
                .stream()
                .findAny()
                .map(BaseEntity::getId)
                .orElseThrow(() -> new IllegalArgumentException("Project with id=%d doesn't have task columns"));
        return taskService.createTask(
                TaskEntity.builder().projectId(projectId).columnId(columnId).name("SomeTask").build(),
                userId
        );
    }
}

