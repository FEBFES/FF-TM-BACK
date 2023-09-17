package com.febfes.fftmback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Date;

@Builder
public record ColumnDto(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @NotBlank(message = "Invalid Name: Empty name")
        String name,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Date createDate,

        @Schema(description = "Column order on the board. Starts at 1")
        Integer order,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long projectId
) {
}
