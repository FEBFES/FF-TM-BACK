package com.example.fftmback.filter;

import lombok.Data;

import java.time.Instant;

@Data
public class TaskFilter {

    private String name;
    private Instant dateInFrom;
    private Instant dateInTo;
}
