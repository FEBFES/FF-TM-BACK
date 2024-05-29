package com.febfes.fftmback.dto;

import java.time.LocalDateTime;

public record TaskFileDto(

        Long id,

        LocalDateTime createDate,

        Long userId,

        String name,

        String type,

        String fileUrn
) {
}
