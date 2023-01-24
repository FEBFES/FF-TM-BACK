package com.febfes.fftmback.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColumnDto {
    Long id;
    String name;
    String description;
    Integer columnOrder;
}
