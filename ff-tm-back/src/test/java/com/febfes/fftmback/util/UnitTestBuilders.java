package com.febfes.fftmback.util;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.FileEntity;
import com.febfes.fftmback.domain.dao.TaskColumnEntity;
import com.febfes.fftmback.domain.dao.UserEntity;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UnitTestBuilders {

    public static UserEntity user(Long id, String username, String password) {
        return UserEntity.builder()
                .id(id)
                .username(username)
                .encryptedPassword(password)
                .build();
    }

    public static TaskColumnEntity column(Long id, Long projectId, String name) {
        return TaskColumnEntity.builder()
                .id(id)
                .projectId(projectId)
                .name(name)
                .build();
    }

    public static FileEntity file(Long id, String fileUrn, EntityType type, String path) {
        return FileEntity.builder()
                .id(id)
                .fileUrn(fileUrn)
                .entityType(type)
                .filePath(path)
                .build();
    }
}
