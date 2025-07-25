package com.febfes.fftmback.dto;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.util.JsonUtils;
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
        return JsonUtils.convertObjectToMap(new RoleError(expected, actual));
    }
}
