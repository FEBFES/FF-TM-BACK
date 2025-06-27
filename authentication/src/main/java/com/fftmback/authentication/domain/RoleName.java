package com.fftmback.authentication.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleName {

    MEMBER(0),
    MEMBER_PLUS(1),
    OWNER(2);

    private final int priority;
}
