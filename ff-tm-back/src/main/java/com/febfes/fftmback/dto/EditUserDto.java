package com.febfes.fftmback.dto;

public record EditUserDto(
        String firstName,
        String lastName,
        String displayName,
        String password
) {
}
