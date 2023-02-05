package com.febfes.fftmback.dto.parameter;

import jakarta.validation.constraints.NotBlank;

public record TaskParameters(
        @NotBlank
        Long projectId,

        @NotBlank
        Long columnId,

        @NotBlank
        Long taskId
) {
}
