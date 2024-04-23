package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.TaskPriority;
import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.dto.ColumnWithTasksDto;
import com.febfes.fftmback.repository.TaskViewRepository;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.UserService;
import com.febfes.fftmback.service.project.DashboardService;
import com.febfes.fftmback.service.project.ProjectManagementService;
import com.febfes.fftmback.util.DtoBuilders;
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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class TaskFilterTest extends BasicStaticDataTestClass {

    private static final String TASK_NAME = "task name";

    @Autowired
    private TaskViewRepository taskViewRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired AuthenticationService authenticationService,
            @Autowired UserService userService,
            @Autowired @Qualifier("projectManagementServiceDecorator") ProjectManagementService projectManagementService,
            @Autowired TaskService taskService,
            @Autowired DashboardService dashboardService
    ) {
        UserEntity user = DtoBuilders.createUser();
        authenticationService.registerUser(user);
        Long createdUserId = userService.getUserIdByUsername(user.getUsername());

        UserEntity user2 = DtoBuilders.createUser();
        authenticationService.registerUser(user2);
        Long createdUserId2 = userService.getUserIdByUsername(user2.getUsername());

        ProjectEntity projectEntity = projectManagementService.createProject(Instancio.create(ProjectEntity.class), createdUserId);
        Long createdProjectId = projectEntity.getId();

        ProjectEntity projectEntity2 = projectManagementService.createProject(Instancio.create(ProjectEntity.class), createdUserId2);
        Long createdProjectId2 = projectEntity2.getId();

        if (!ForkJoinPool.commonPool().awaitQuiescence(10, TimeUnit.SECONDS)) {
            fail("Columns aren't created");
        }

        TaskSpec taskSpec = mock(TaskSpec.class);
        var dashboard1 = dashboardService.getDashboard(createdProjectId, taskSpec);
        var dashboard2 = dashboardService.getDashboard(createdProjectId2, taskSpec);
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
                createdUserId
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(columnIds1.get(0))
                        .name(TASK_NAME + "2")
                        .description("12345")
                        .build(),
                createdUserId
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(columnIds1.get(1))
                        .name(TASK_NAME)
                        .description("12345")
                        .build(),
                createdUserId
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(columnIds1.get(0))
                        .name(TASK_NAME + "another")
                        .description("12345")
                        .build(),
                createdUserId2
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId2)
                        .columnId(columnId3)
                        .name(TASK_NAME + "another")
                        .description("12345")
                        .build(),
                createdUserId2
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
        // TODO: add more data
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
}
