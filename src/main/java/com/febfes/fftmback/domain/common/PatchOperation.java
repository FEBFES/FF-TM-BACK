package com.febfes.fftmback.domain.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum PatchOperation {
    UPDATE("update");

    private String code;

    public static PatchOperation getByCode(String code) {
        return Arrays.stream(PatchOperation.values())
                .filter(operation -> operation.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Can't find such patch operation: " + code));
    }
}
