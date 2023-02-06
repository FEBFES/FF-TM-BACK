package com.febfes.fftmback.dto.parameter;

import jakarta.validation.constraints.NotBlank;

public record ColumnParameters(
        @NotBlank
        Long projectId,

        @NotBlank
        Long columnId
) {
}
