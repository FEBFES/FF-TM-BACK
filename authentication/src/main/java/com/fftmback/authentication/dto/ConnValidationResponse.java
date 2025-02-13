package com.fftmback.authentication.dto;

import lombok.Builder;

@Builder
public record ConnValidationResponse(
        String status,
        boolean isAuthenticated,
        String methodType,
        String username,
        String role
) {
}
