package com.febfes.fftmback.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDto {

    Long id;
    String name;
    String description;
    Date createDate;
}
