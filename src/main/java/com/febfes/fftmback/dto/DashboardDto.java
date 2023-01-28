package com.febfes.fftmback.dto;

import lombok.Value;

import java.util.List;

@Value
public class DashboardDto {

    Long projectId;
    String name;
    String description;
    List<ColumnWithTasksDto> columnWithTasksDtoList;

}
