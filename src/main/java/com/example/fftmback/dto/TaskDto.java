package com.example.fftmback.dto;

import lombok.Value;

import java.util.Date;

@Value
public class TaskDto {

    Long id;
    String name;
    String description;
    Date createDate;
}
