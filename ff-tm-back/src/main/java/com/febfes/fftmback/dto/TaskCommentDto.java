package com.febfes.fftmback.dto;

import lombok.Builder;

@Builder
public record TaskCommentDto(
        Long id,
        Long creatorId,
        String creatorName,
        Long taskId,
        String text
) {
}
