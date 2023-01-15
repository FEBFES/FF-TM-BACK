package com.example.fftmback.domain;

import lombok.Data;

import java.time.Instant;

@Data
public class Board {

    private Long id;
    private String name;
    private Instant dateIn;
}
