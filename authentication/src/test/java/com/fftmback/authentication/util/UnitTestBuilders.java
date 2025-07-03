package com.fftmback.authentication.util;

import com.fftmback.authentication.domain.UserEntity;
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
}
