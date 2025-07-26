package com.fftmback.authentication.dto;

public record EditUserDto(
        String firstName,
        String lastName,
        String displayName,
        String password
) {
}
