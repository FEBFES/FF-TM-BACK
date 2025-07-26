package com.febfes.fftmback.exception;

import com.febfes.fftmback.domain.RoleName;

import java.io.Serial;

public class UserRoleChangeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7001234567894321435L;

    public UserRoleChangeException(Long projectId, Long userId, RoleName roleName) {
        super("Failed to change user's role. projectId=%d, userId=%d, roleName=%s".formatted(
                projectId, userId, roleName.name()));
    }
}
