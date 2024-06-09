package com.febfes.fftmback.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultColumns {

    BACKLOG("BACKLOG"),
    IN_PROGRESS("IN PROGRESS"),
    REVIEW("REVIEW"),
    DONE("DONE")
    ;

    private final String caption;
}
