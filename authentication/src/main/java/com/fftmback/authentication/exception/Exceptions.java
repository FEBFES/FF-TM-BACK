package com.fftmback.authentication.exception;

import com.febfes.fftmback.dto.ErrorType;
import com.febfes.fftmback.exception.EntityNotFoundException;
import com.fftmback.authentication.domain.RefreshTokenEntity;
import com.fftmback.authentication.domain.UserEntity;
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

    public Supplier<EntityNotFoundException> refreshTokenNotFound(String token) {
        return () -> new EntityNotFoundException(RefreshTokenEntity.ENTITY_NAME, "token", token);
    }

    public Supplier<EntityNotFoundException> refreshTokenNotFoundByUserId(Long userId) {
        return () -> new EntityNotFoundException(RefreshTokenEntity.ENTITY_NAME, "userId", userId.toString());
    }

}
