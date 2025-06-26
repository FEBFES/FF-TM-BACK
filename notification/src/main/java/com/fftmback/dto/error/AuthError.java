package com.fftmback.dto.error;

import com.febfes.fftmback.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
@Getter
public class AuthError {

    private String entity;
    private String fieldName;
    private String value;

    public static Map<String, Object> createBaseError(
            String entity,
            String fieldName,
            String fieldValue,
            ErrorType errorType
    ) {
        if (ErrorType.AUTH.equals(errorType)) {
            return JsonUtils.convertObjectToMap(new AuthError(entity, fieldName, fieldValue));
        }
        return Collections.emptyMap();
    }

    public static Map<String, Object> createBaseError(
            String entity,
            Long id,
            ErrorType errorType
    ) {
        return createBaseError(entity, "id", id.toString(), errorType);
    }
}
