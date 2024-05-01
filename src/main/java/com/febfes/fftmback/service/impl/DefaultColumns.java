package com.febfes.fftmback.service.impl;

import lombok.Getter;

@Getter
public enum DefaultColumns {

    BACKLOG("BACKLOG"),
    IN_PROGRESS("IN PROGRESS"),
    REVIEW("REVIEW"),
    DONE("DONE");

    private final String caption;
    DefaultColumns(String caption) {
        this.caption = caption;
    }
}
