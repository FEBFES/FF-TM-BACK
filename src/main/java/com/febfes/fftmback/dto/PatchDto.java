package com.febfes.fftmback.dto;

import jakarta.validation.constraints.NotBlank;

public record PatchDto(
        @NotBlank(message = "Operation can't be empty")
        String op,

        @NotBlank(message = "Key can't be empty")
        String key,

        Object value
) {
}
