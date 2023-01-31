package com.febfes.fftmback.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record ProjectDto(

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        String name,

        String description,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Date createDate
) {
}
