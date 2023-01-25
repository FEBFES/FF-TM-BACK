package com.febfes.fftmback.dto;

import lombok.Value;

import java.util.Date;

@Value
public class ProjectDto {

    Long id;
    String name;
    String description;
    Date createDate;
}
