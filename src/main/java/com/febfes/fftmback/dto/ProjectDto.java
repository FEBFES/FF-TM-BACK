package com.febfes.fftmback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.Date;

@Value
public class ProjectDto {

    @Schema(description = "No need to set id in the request body")
    Long id;

    String name;

    String description;

    @Schema(description = "No need to set create date in the request body")
    Date createDate;
}
