package com.febfes.fftmback.dto;

import java.util.List;

public record ColumnWithTasksDto(
        Long id,
        String name,
        Integer order,
        List<TaskShortDto> tasks
) {

}
