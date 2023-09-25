package com.febfes.fftmback.dto.error;

import com.febfes.fftmback.util.JsonUtils;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class AuthError {

    private String entity;
    private String fieldName;
    private String value;

    public static Map<String, ?> createBaseError(
            String entity,
            String fieldName,
            String fieldValue,
            ErrorType errorType
    ) {
        if (ErrorType.AUTH.equals(errorType)) {
            return JsonUtils.convertObjectToMap(new AuthError(entity, fieldName, fieldValue));
        }
        return null;
    }

    public static Map<String, ?> createBaseError(
            String entity,
            Long id,
            ErrorType errorType
    ) {
        return createBaseError(entity, "id", id.toString(), errorType);
    }
}
