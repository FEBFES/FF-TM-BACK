package com.febfes.fftmback.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleName {

    MEMBER(0),
    MEMBER_PLUS(1),
    OWNER(2);

    final int priority;
}
