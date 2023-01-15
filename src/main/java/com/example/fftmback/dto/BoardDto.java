package com.example.fftmback.dto;

import lombok.Value;

import java.time.Instant;

@Value
public class BoardDto {

    Long id;
    String name;
    Instant dateIn;
}
