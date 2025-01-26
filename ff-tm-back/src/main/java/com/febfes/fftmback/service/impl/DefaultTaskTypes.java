package com.febfes.fftmback.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultTaskTypes {

    BUG("bug"),
    FEATURE("feature"),
    RESEARCH("research"),
    QUESTION("question")
    ;

    private final String caption;
}
