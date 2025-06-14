package com.febfes.fftmback.dto;

import java.util.List;

public record DashboardDto(
        List<ColumnWithTasksDto> columns
) {

}
