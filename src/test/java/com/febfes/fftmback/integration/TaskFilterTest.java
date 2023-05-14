package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.TaskPriority;
import com.febfes.fftmback.domain.common.query.FilterRequest;
import com.febfes.fftmback.domain.common.query.FilterSpecification;
import com.febfes.fftmback.domain.common.query.Operator;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.exception.NoSuitableTypeFilterException;
import com.febfes.fftmback.exception.ValueFilterException;
import com.febfes.fftmback.repository.TaskViewRepository;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.util.DatabaseCleanup;
import com.febfes.fftmback.util.DateUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static com.febfes.fftmback.integration.ProjectControllerTest.PROJECT_NAME;
import static com.febfes.fftmback.integration.TaskControllerTest.TASK_NAME;
import static com.febfes.fftmback.util.DateUtils.STANDARD_DATE_PATTERN;

class TaskFilterTest extends BasicTestClass {

    @Autowired
    private TaskViewRepository taskViewRepository;

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(STANDARD_DATE_PATTERN);

    @BeforeAll
    static void beforeAll(
            @Autowired AuthenticationService authenticationService,
            @Autowired UserService userService,
            @Autowired ProjectService projectService,
            @Autowired TaskService taskService
    ) {
        authenticationService.registerUser(
                UserEntity.builder().email(USER_EMAIL).username(USER_USERNAME).encryptedPassword(USER_PASSWORD).build()
        );
        String createdUsername = userService.loadUserByUsername(USER_USERNAME).getUsername();

        authenticationService.registerUser(UserEntity
                .builder()
                .email("new_" + USER_EMAIL)
                .username("new" + USER_USERNAME)
                .encryptedPassword(USER_PASSWORD)
                .build()
        );
        String createdUsername2 = userService.loadUserByUsername("new" + USER_USERNAME).getUsername();

        ProjectEntity projectEntity = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                createdUsername
        );
        Long createdProjectId = projectEntity.getId();

        ProjectEntity projectEntity2 = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                createdUsername2
        );
        Long createdProjectId2 = projectEntity2.getId();

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(1L)
                        .name(TASK_NAME + "1")
                        .description("123")
                        .priority(TaskPriority.LOW)
                        .build(),
                createdUsername
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(1L)
                        .name(TASK_NAME + "2")
                        .description("12345")
                        .build(),
                createdUsername
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(2L)
                        .name(TASK_NAME)
                        .description("12345")
                        .build(),
                createdUsername
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(1L)
                        .name(TASK_NAME + "another")
                        .description("12345")
                        .build(),
                createdUsername2
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId2)
                        .columnId(1L)
                        .name(TASK_NAME + "another")
                        .description("12345")
                        .build(),
                createdUsername2
        );

        /*
        1 project:
            1 column:
                1 user: 2 tasks
                2 user: 1 task
            2 column:
                1 user: 1 task
        2 project:
            1 column:
                2 user: 1 task
         */
    }

    @AfterEach
    @Override
    void afterEach() {

    }

    @AfterAll
    static void afterAll(@Autowired DatabaseCleanup databaseCleanup) {
        databaseCleanup.execute();
    }

    @Test
    void equalFilterTest() {
        List<FilterRequest> filters = List.of(
                FilterRequest.builder()
                        .property("name")
                        .operator(Operator.EQUAL)
                        .value(TASK_NAME)
                        .build()
        );
        Assertions.assertEquals(1, taskViewRepository.findAll(new FilterSpecification<>(filters)).size());

        List<FilterRequest> filtersProjectId = List.of(
                FilterRequest.builder()
                        .property("projectId")
                        .operator(Operator.EQUAL)
                        .value(1)
                        .build()
        );
        Assertions.assertEquals(4, taskViewRepository.findAll(new FilterSpecification<>(filtersProjectId)).size());

        List<FilterRequest> filtersProjectIdAndName = List.of(
                FilterRequest.builder()
                        .property("projectId")
                        .operator(Operator.EQUAL)
                        .value(1)
                        .build(),
                FilterRequest.builder()
                        .property("name")
                        .operator(Operator.EQUAL)
                        .value(TASK_NAME)
                        .build()
        );
        Assertions.assertEquals(1, taskViewRepository.findAll(new FilterSpecification<>(filtersProjectIdAndName)).size());
    }

    @Test
    void notEqualFilterTest() {
        List<FilterRequest> filters = List.of(
                FilterRequest.builder()
                        .property("name")
                        .operator(Operator.NOT_EQUAL)
                        .value(TASK_NAME)
                        .build()
        );
        Assertions.assertEquals(4, taskViewRepository.findAll(new FilterSpecification<>(filters)).size());

        List<FilterRequest> filtersOwnerId = List.of(
                FilterRequest.builder()
                        .property("ownerId")
                        .operator(Operator.NOT_EQUAL)
                        .value(1)
                        .build()
        );
        Assertions.assertEquals(2, taskViewRepository.findAll(new FilterSpecification<>(filtersOwnerId)).size());
    }

    @Test
    void likeFilterTest() {
        List<FilterRequest> filters = List.of(
                FilterRequest.builder()
                        .property("name")
                        .operator(Operator.LIKE)
                        .value(TASK_NAME)
                        .build()
        );
        Assertions.assertEquals(5, taskViewRepository.findAll(new FilterSpecification<>(filters)).size());

        // it will fail as LIKE operator only accept strings
        List<FilterRequest> filtersFailed = List.of(
                FilterRequest.builder()
                        .property("ownerId")
                        .operator(Operator.LIKE)
                        .value(1)
                        .build()
        );
        Assertions.assertThrows(
                NoSuitableTypeFilterException.class,
                () -> taskViewRepository.findAll(new FilterSpecification<>(filtersFailed))
        );
    }

    @Test
    void inFilterTest() {
        List<FilterRequest> filters = List.of(
                FilterRequest.builder()
                        .property("name")
                        .operator(Operator.IN)
                        .values(List.of(TASK_NAME + "1", TASK_NAME + "2"))
                        .build()
        );
        Assertions.assertEquals(2, taskViewRepository.findAll(new FilterSpecification<>(filters)).size());

        // it will fail as IN operator only accept values belong to the same class
        List<FilterRequest> filtersFailed = List.of(
                FilterRequest.builder()
                        .property("name")
                        .operator(Operator.IN)
                        .values(List.of(TASK_NAME, 1))
                        .build()
        );
        Assertions.assertThrows(
                ValueFilterException.class,
                () -> taskViewRepository.findAll(new FilterSpecification<>(filtersFailed))
        );
    }

    @Test
    void betweenFilterTest() {
        LocalDateTime value = DateUtils.convertDateToLocalDateTime(DateUtils.getCurrentDatePlusSeconds(-1000));
        String valueString = FORMATTER.format(value);

        LocalDateTime valueTo = DateUtils.convertDateToLocalDateTime(DateUtils.getCurrentDatePlusSeconds(1000));
        String valueToString = FORMATTER.format(valueTo);

        List<FilterRequest> filters = List.of(
                FilterRequest.builder()
                        .property("createDate")
                        .operator(Operator.BETWEEN)
                        .value(valueString)
                        .valueTo(valueToString)
                        .build()
        );
        Assertions.assertEquals(5, taskViewRepository.findAll(new FilterSpecification<>(filters)).size());

        // it will fail as BETWEEN operator only accept value and valueTo belong to the same class
        List<FilterRequest> filtersFailed = List.of(
                FilterRequest.builder()
                        .property("name")
                        .operator(Operator.BETWEEN)
                        .value(1)
                        .valueTo(valueToString)
                        .build()
        );
        Assertions.assertThrows(
                ValueFilterException.class,
                () -> taskViewRepository.findAll(new FilterSpecification<>(filtersFailed))
        );
    }
}
