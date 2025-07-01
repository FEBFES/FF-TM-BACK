package com.fftmback.authentication.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDto(

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id,

        String email,

        String username,

        String firstName,

        String lastName,

        String displayName,

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        String userPic
) {
}
