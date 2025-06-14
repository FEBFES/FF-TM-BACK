package com.fftmback.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDetailsDto(

        @Email(message = "Invalid Email")
        String email,

        @NotBlank(message = "Invalid Username: Empty username")
        String username,

        @NotBlank(message = "Invalid Password: Empty password")
        String password
) {
}
