package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.common.TaskPriority;
import com.febfes.fftmback.domain.common.specification.TaskSpec;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import com.febfes.fftmback.repository.TaskViewRepository;
import com.febfes.fftmback.service.AuthenticationService;
import com.febfes.fftmback.service.ProjectService;
import com.febfes.fftmback.service.TaskService;
import com.febfes.fftmback.service.UserService;
import net.kaczmarzyk.spring.data.jpa.utils.SpecificationBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.*;
import static com.febfes.fftmback.integration.ProjectControllerTest.PROJECT_NAME;
import static com.febfes.fftmback.integration.TaskControllerTest.TASK_NAME;

class TaskFilterTest extends BasicStaticDataTestClass {

    @Autowired
    private TaskViewRepository taskViewRepository;

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
        Long createdUserId = userService.getUserIdByUsername(USER_USERNAME);

        authenticationService.registerUser(UserEntity
                .builder()
                .email("new_" + USER_EMAIL)
                .username("new" + USER_USERNAME)
                .encryptedPassword(USER_PASSWORD)
                .build()
        );
        Long createdUserId2 = userService.getUserIdByUsername("new" + USER_USERNAME);

        ProjectEntity projectEntity = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                createdUserId
        );
        Long createdProjectId = projectEntity.getId();

        ProjectEntity projectEntity2 = projectService.createProject(
                ProjectEntity.builder().name(PROJECT_NAME).build(),
                createdUserId2
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
                createdUserId
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(1L)
                        .name(TASK_NAME + "2")
                        .description("12345")
                        .build(),
                createdUserId
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(2L)
                        .name(TASK_NAME)
                        .description("12345")
                        .build(),
                createdUserId
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId)
                        .columnId(1L)
                        .name(TASK_NAME + "another")
                        .description("12345")
                        .build(),
                createdUserId2
        );

        taskService.createTask(
                TaskEntity
                        .builder()
                        .projectId(createdProjectId2)
                        .columnId(5L)
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
