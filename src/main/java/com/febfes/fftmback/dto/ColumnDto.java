package com.febfes.fftmback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.Date;

@Value
public class ColumnDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    Long id;

    String name;

    String description;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    Date createDate;

    Integer columnOrder;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    Long projectId;
}
