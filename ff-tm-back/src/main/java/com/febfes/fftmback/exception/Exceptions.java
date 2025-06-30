package com.febfes.fftmback.exception;

import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.domain.dao.ProjectEntity;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.TaskEntity;
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

    public Supplier<EntityNotFoundException> taskNotFound(Long id) {
        return () -> new EntityNotFoundException(TaskEntity.ENTITY_NAME, id);
    }

}
