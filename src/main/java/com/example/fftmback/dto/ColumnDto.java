package com.example.fftmback.dto;

import lombok.Value;

@Value
public class ColumnDto {
    Long id;
    String name;
    String description;
    Integer columnOrder;
}
