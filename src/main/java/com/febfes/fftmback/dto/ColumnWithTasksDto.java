package com.febfes.fftmback.dto;

import lombok.Value;

import java.util.List;

@Value
public class ColumnWithTasksDto {

    Long id;
    String name;
    Integer order;
    List<TaskShortDto> taskResponseList;
}
