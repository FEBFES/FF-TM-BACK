package com.febfes.fftmback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public record TaskDto(

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        @NotBlank(message = "Invalid Name: Empty name")
        String name,

        String description,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Date createDate,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long projectId,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long columnId,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long ownerId

) {

    public static final class Builder {

        String name;

        public Builder(String name) {
            this.name = name;
        }

        public TaskDto build() {
            return new TaskDto(null, name, null, null, null, null, null);
        }
    }
}
