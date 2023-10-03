package com.febfes.fftmback.dto.error;

import com.febfes.fftmback.util.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.constant.Constable;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class AuthError {

    private String entity;
    private String fieldName;
    private String value;

    public static Map<String, Constable> createBaseError(
            String entity,
            String fieldName,
            String fieldValue,
            ErrorType errorType
    ) {
        if (ErrorType.AUTH.equals(errorType)) {
            return JsonUtils.convertObjectToMap(new AuthError(entity, fieldName, fieldValue));
        }
        return new HashMap<>();
    }

    public static Map<String, Constable> createBaseError(
            String entity,
            Long id,
            ErrorType errorType
    ) {
        return createBaseError(entity, "id", id.toString(), errorType);
    }
}
