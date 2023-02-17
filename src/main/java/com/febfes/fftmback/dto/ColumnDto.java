package com.febfes.fftmback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public record ColumnDto(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @NotBlank(message = "Invalid Name: Empty name")
        String name,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Date createDate,

        @Schema(description = "Column order on the board. Starts at 0")
        Integer columnOrder,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long projectId,

        Long childTaskColumnId
) {
        public ColumnDto(String name) {
                this(null, name, null, null, null, null);
        }
}
