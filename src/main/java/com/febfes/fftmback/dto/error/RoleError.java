package com.febfes.fftmback.dto.error;

import com.febfes.fftmback.domain.common.RoleName;
import com.febfes.fftmback.util.JsonUtils;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class RoleError {

    private RoleName expected;
    private RoleName actual;

    public static Map<String, ?> createBaseError(
            RoleName expected,
            RoleName actual
    ) {
        return JsonUtils.convertObjectToMap(new RoleError(expected, actual));
    }
}
