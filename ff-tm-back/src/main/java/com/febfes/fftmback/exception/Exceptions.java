package com.febfes.fftmback.exception;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.*;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class Exceptions {

    public Supplier<EntityNotFoundException> userNotFoundById(Long id) {
        return () -> new EntityNotFoundException("User", id);
    }

    public Supplier<EntityNotFoundException> columnNotFound(Long id) {
        return () -> new EntityNotFoundException(TaskColumnEntity.ENTITY_NAME, id);
    }

    public Supplier<EntityNotFoundException> fileNotFound(String fileUrn) {
        return () -> new EntityNotFoundException(FileEntity.ENTITY_NAME, "file urn", fileUrn);
    }

    public Supplier<EntityNotFoundException> projectNotFound(Long id) {
        return () -> new EntityNotFoundException(ProjectEntity.ENTITY_NAME, id);
    }

    public EntityNotFoundException exceptionProjectNotFound(Long id) {
        return new EntityNotFoundException(ProjectEntity.ENTITY_NAME, id);
    }

    public Supplier<EntityNotFoundException> taskNotFound(Long id) {
        return () -> new EntityNotFoundException(TaskEntity.ENTITY_NAME, id);
    }

    public Supplier<EntityNotFoundException> taskCommentNotFound(Long id) {
        return () -> new EntityNotFoundException(TaskCommentEntity.ENTITY_NAME, id);
    }

    public Supplier<EntityNotFoundException> roleNotFound(RoleName roleName) {
        return () -> new EntityNotFoundException(RoleEntity.ENTITY_NAME, "name", roleName.name());
    }

    public Supplier<EntityNotFoundException> roleNotFoundByProjectId(Long projectId) {
        return () -> new EntityNotFoundException(RoleEntity.ENTITY_NAME, "projectId", projectId.toString());
    }

}
