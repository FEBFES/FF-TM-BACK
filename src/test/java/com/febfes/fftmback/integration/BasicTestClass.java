package com.febfes.fftmback.integration;


import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.util.DatabaseCleanup;
import com.febfes.fftmback.util.DtoBuilders;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BasicTestClass {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    protected AuthenticationService authenticationService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected ProjectService projectService;

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
        ProjectEntity project = DtoBuilders.createProject(createdUserId);
        return projectService.createProject(project, createdUserId).getId();
    }
}

