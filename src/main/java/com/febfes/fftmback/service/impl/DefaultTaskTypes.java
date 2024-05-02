package com.febfes.fftmback.service.impl;

import lombok.Getter;

@Getter
public enum DefaultTaskTypes {

    BUG("bug"),
    FEATURE("feature"),
    RESEARCH("research"),
    QUESTION("question");

    private final String caption;
    DefaultTaskTypes(String caption) {
        this.caption = caption;
    }
}
