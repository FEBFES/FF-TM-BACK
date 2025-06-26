package com.fftmback.authentication.dto.error;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class RoleError {

    private RoleName expected;
    private RoleName actual;

    public static Map<String, Object> createBaseError(
            RoleName expected,
            RoleName actual
    ) {
        return new ObjectMapper().convertValue(new RoleError(expected, actual), new TypeReference<>() {
        });
    }
}
