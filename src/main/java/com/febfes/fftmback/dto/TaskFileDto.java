package com.febfes.fftmback.dto;

import java.util.Date;

public record TaskFileDto(

        Long id,

        Date createDate,

        Long userId,

        String name,

        String type,

        String fileUrn
) {
}
