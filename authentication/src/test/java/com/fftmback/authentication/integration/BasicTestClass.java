package com.fftmback.authentication.integration;


import com.febfes.fftmback.domain.RoleName;
import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.feign.RoleClient;
import com.fftmback.authentication.service.AuthenticationService;
import com.fftmback.authentication.service.UserService;
import com.fftmback.authentication.util.DatabaseCleanup;
import com.fftmback.authentication.util.DtoBuilders;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.fftmback.authentication.util.DtoBuilders.PASSWORD;
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

    @LocalServerPort
    private Integer port;

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

    @MockBean
    private RoleClient roleClient;

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
        createdUserId = ((UserEntity) userService.loadUserByUsername(user.getUsername())).getId();
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
        return ((UserEntity) userService.loadUserByUsername(user.getUsername())).getId();
    }
}

