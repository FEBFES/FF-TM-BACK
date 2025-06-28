package com.febfes.fftmback.exception;

import com.febfes.fftmback.domain.dao.*;
import com.febfes.fftmback.dto.error.ErrorType;
import lombok.experimental.UtilityClass;

import java.util.function.Supplier;

@UtilityClass
public class Exceptions {

    public Supplier<EntityNotFoundException> userNotFound(String username) {
        return () -> new EntityNotFoundException(UserEntity.ENTITY_NAME, "username", username, ErrorType.AUTH);
    }

    public Supplier<EntityNotFoundException> userNotFoundById(Long id) {
        return () -> new EntityNotFoundException(UserEntity.ENTITY_NAME, id);
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

    public Supplier<EntityNotFoundException> taskNotFound(Long id) {
        return () -> new EntityNotFoundException(TaskEntity.ENTITY_NAME, id);
    }

}
