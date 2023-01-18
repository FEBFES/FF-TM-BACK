package com.example.fftmback.dto;

import lombok.Value;

import java.time.Instant;

@Value
public class TaskDto {

    Long id;
    String name;
    String description;
    Instant dateIn;
}
