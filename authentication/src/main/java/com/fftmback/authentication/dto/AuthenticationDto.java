package com.fftmback.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDto(
        @NotBlank(message = "Invalid Username: Empty username")
        String username,

        @NotBlank(message = "Invalid Password: Empty password")
        String password
) {
}
