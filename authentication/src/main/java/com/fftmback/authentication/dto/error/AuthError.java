package com.fftmback.authentication.dto.error;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
            return new ObjectMapper().convertValue(new AuthError(entity, fieldName, fieldValue), new TypeReference<>() {
            });
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
