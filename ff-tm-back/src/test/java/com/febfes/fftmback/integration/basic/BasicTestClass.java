package com.febfes.fftmback.integration.basic;


import com.febfes.fftmback.config.WebSecurityTestConfig;
import com.febfes.fftmback.config.jwt.User;
import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.abstracts.BaseEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.UserDto;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskService;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Slf4j
@ImportAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@Import(WebSecurityTestConfig.class)
public class BasicTestClass {

    @Autowired
    private DatabaseCleanup databaseCleanup;

    @Autowired
    private JwtTestUtil jwtTestUtil;

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

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    protected UserService userService;

    protected String token;
    protected String username = "username";
    protected Long createdUserId = 1L;
    private Long userIdCounter = 1L;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;

        createdUserId = 1L;
        UserDto newUser = new UserDto(createdUserId, "new@test.com", "new-user", null, null, null, null);
        when(userService.getUser(createdUserId)).thenReturn(newUser);
        token = generateToken(createdUserId, username);
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
        return ++userIdCounter;
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
                new User(userId, null, null)
        );
    }

    protected String generateToken(Long userId, String username) {
        return jwtTestUtil.generateToken(userId, username);
    }
}

