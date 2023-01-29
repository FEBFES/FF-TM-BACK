package com.febfes.fftmback.dto;

import java.util.List;

public record DashboardDto(
        String name,
        String description,
        List<ColumnWithTasksDto> columns
) {

}
