package com.febfes.fftmback.dto;

import com.febfes.fftmback.domain.common.PatchOperation;
import jakarta.validation.constraints.NotBlank;

public record PatchDto(
        @NotBlank(message = "Operation can't be empty")
        PatchOperation op,

        @NotBlank(message = "Key can't be empty")
        String key,

        Object value
) {
}
