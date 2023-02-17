package com.febfes.fftmback.dto.parameter;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotBlank;

public record TaskParameters(
        @NotBlank
        @Parameter(in = ParameterIn.PATH)
        Long projectId,

        @NotBlank
        @Parameter(in = ParameterIn.PATH)
        Long columnId,

        @NotBlank
        @Parameter(in = ParameterIn.PATH)
        Long taskId
) {
}
