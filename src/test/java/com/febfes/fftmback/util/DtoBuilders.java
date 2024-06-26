package com.febfes.fftmback.util;

import com.febfes.fftmback.domain.dao.*;
import com.febfes.fftmback.dto.ColumnDto;
import com.febfes.fftmback.dto.EditTaskDto;
import lombok.experimental.UtilityClass;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.Collections;

import static com.febfes.fftmback.integration.AuthenticationControllerTest.EMAIL_PATTERN;
import static org.instancio.Select.field;

@UtilityClass
@Profile("test")
public class DtoBuilders {

    public static final String PASSWORD = "password";

    public ColumnDto createColumnDto(String name, Integer order) {
        return ColumnDto.builder()
                .name(name)
                .order(order)
                .build();
    }

    public static UserEntity createUser() {
        return commonUser().create();
    }

    public static UserEntity createUser(String displayName) {
        return commonUser()
                .set(field(UserEntity::getDisplayName), displayName)
                .create();
    }

    public static TaskColumnEntity createColumn(Long projectId) {
        return Instancio.of(TaskColumnEntity.class)
                .set(field(TaskColumnEntity::getProjectId), projectId)
                .create();
    }

    public static TaskEntity createTask(Long projectId, Long columnId) {
        return commonTask(projectId, columnId).create();
    }

    public static TaskEntity createTask(Long projectId, Long columnId, String taskName) {
        return commonTask(projectId, columnId)
                .set(field(TaskEntity::getName), taskName)
                .create();
    }

    public static ProjectEntity createProject(String name) {
        return Instancio.of(ProjectEntity.class)
                .set(field(ProjectEntity::getName), name)
                .create();
    }

    public static ProjectEntity createProject(Long userId) {
        return Instancio.of(ProjectEntity.class)
                .set(field(ProjectEntity::getOwnerId), userId)
                .create();
    }

    public static TaskTypeEntity createTaskType(Long projectId) {
        return Instancio.of(TaskTypeEntity.class)
                .set(field(TaskTypeEntity::getProjectId), projectId)
                .create();
    }

    public static EditTaskDto createEditTaskDto(Integer order) {
        return commonEditTask()
                .set(field(EditTaskDto::order), order)
                .set(field(EditTaskDto::deadlineDate), LocalDateTime.now().plusDays(1L))
                .create();
    }

    private InstancioApi<UserEntity> commonUser() {
        return Instancio.of(UserEntity.class)
                .generate(field(UserEntity::getEmail), gen -> gen.text().pattern(EMAIL_PATTERN))
                .set(field(UserEntity::getProjectRoles), Collections.emptyMap())
                .set(field(UserEntity::getEncryptedPassword), PASSWORD);
    }

    private InstancioApi<TaskEntity> commonTask(Long projectId, Long columnId) {
        return Instancio.of(TaskEntity.class)
                .set(field(TaskEntity::getProjectId), projectId)
                .set(field(TaskEntity::getColumnId), columnId)
                .set(field(TaskEntity::getAssigneeId), null)
                .set(field(TaskEntity::getOwnerId), null)
                .set(field(TaskEntity::getTaskType), null);
    }

    private InstancioApi<EditTaskDto> commonEditTask() {
        return Instancio.of(EditTaskDto.class)
                .set(field(EditTaskDto::assigneeId), null);
    }
}
