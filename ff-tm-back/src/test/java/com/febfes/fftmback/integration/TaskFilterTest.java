package com.febfes.fftmback.integration;

import com.febfes.fftmback.config.jwt.User;
import com.febfes.fftmback.domain.common.TaskPriority;
import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.repository.TaskViewRepository;
import com.febfes.fftmback.service.ColumnService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.project.DashboardService;
import com.febfes.fftmback.service.project.ProjectManagementService;
import com.febfes.fftmback.util.DtoBuilders;
import com.fftmback.authentication.domain.UserEntity;
import com.fftmback.authentication.service.AuthenticationService;
import com.fftmback.authentication.service.UserService;
import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.stream.Stream;

import static com.febfes.fftmback.util.DtoBuilders.PASSWORD;

class TaskFilterTest extends BasicStaticDataTestClass {

    private static final String TASK_NAME = "task name";

    @Autowired
    private TaskViewRepository taskViewRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired AuthenticationService authenticationService,
            @Autowired UserService userService,
            @Autowired @Qualifier("projectManagementService") ProjectManagementService projectManagementService,
            @Autowired TaskService taskService,
            @Autowired ColumnService columnService,
            @Autowired DashboardService dashboardService
    ) {
        UserEntity user = DtoBuilders.createUser();
        authenticationService.registerUser(user);
        Long createdUserId = authenticationService.authenticateUser(
                UserEntity.builder().username(user.getUsername()).encryptedPassword(PASSWORD).build()
        ).userId();

        UserEntity user2 = DtoBuilders.createUser();
        authenticationService.registerUser(user2);
        Long createdUserId2 = authenticationService.authenticateUser(
                UserEntity.builder().username(user2.getUsername()).encryptedPassword(PASSWORD).build()
        ).userId();

        Long createdProjectId = createProject(projectManagementService, columnService, createdUserId);
        Long createdProjectId2 = createProject(projectManagementService, columnService, createdUserId2);

        TaskSpec emptyTaskSpec = SpecificationBuilder.specification(TaskSpec.class).build();
        var dashboard1 = dashboardService.getDashboard(createdProjectId, emptyTaskSpec);
        var dashboard2 = dashboardService.getDashboard(createdProjectId2, emptyTaskSpec);
        List<Long> columnIds1 = dashboard1.columns().stream().map(ColumnWithTasksDto::id).toList();
        Long columnId3 = dashboard2.columns().get(0).id();

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(columnIds1.get(0))
                        .name(TASK_NAME + "1")
                        .description("123")
                        .priority(TaskPriority.LOW)
                        .build(),
                new User(createdUserId, null, null)
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(columnIds1.get(0))
                        .name(TASK_NAME + "2")
                        .description("12345")
                        .build(),
                new User(createdUserId, null, null)
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(columnIds1.get(1))
                        .name(TASK_NAME)
                        .description("12345")
                        .build(),
                new User(createdUserId, null, null)
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(columnIds1.get(0))
                        .name(TASK_NAME + "another")
                        .description("12345")
                        .build(),
                new User(createdUserId2, null, null)
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId2)
                        .columnId(columnId3)
                        .name(TASK_NAME + "another")
                        .description("12345")
                        .build(),
                new User(createdUserId2, null, null)
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

    @ParameterizedTest
    @MethodSource("taskFilterData")
    void taskFilterTest(TaskSpec taskSpec, int expected) {
        Assertions.assertEquals(expected, taskViewRepository.findAll(taskSpec).size());
    }

    static Stream<Arguments> taskFilterData() {
        return Stream.of(
                Arguments.of(SpecificationBuilder.specification(TaskSpec.class)
                        .withParam("taskId", "1")
                        .build(), 1),
                Arguments.of(SpecificationBuilder.specification(TaskSpec.class)
                        .withParam("taskName", TASK_NAME)
                        .build(), 5),
                Arguments.of(SpecificationBuilder.specification(TaskSpec.class)
                        .withParam("taskDescription", "12345")
                        .build(), 4),
                Arguments.of(SpecificationBuilder.specification(TaskSpec.class)
                        .withParam("taskPriority", TaskPriority.LOW.name())
                        .build(), 1)
        );
    }

    static Long createProject(
            ProjectManagementService projectManagementService,
            ColumnService columnService,
            Long userId
    ) {
        ProjectEntity project = projectManagementService.createProject(Instancio.create(ProjectEntity.class), userId);
        columnService.createDefaultColumnsForProject(project.getId());
        return project.getId();
    }
}
