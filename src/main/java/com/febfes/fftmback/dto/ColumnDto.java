package com.febfes.fftmback.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record ColumnDto(
        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        String name,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Date createDate,

        Integer columnOrder,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long projectId
) {
}
