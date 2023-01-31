package com.febfes.fftmback.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record TaskDto(

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        String name,

        String description,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Date createDate,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long projectId,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long columnId

) {
}
