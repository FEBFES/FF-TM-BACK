package com.febfes.fftmback.dto.response;

import lombok.Value;

import java.util.List;

@Value
public class DashboardResponse {

    Long projectId;
    String name;
    String description;
    List<ColumnWithTasksResponse> columnWithTasksResponseList;

}
