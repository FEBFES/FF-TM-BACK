package com.febfes.fftmback.dto.response;

import lombok.Value;

import java.util.List;

@Value
public class ColumnWithTasksResponse {

    Long id;
    String name;
    Integer order;
    List<ShortTaskResponse> taskResponseList;
}
