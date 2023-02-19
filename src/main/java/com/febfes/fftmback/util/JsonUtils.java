package com.febfes.fftmback.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    @SneakyThrows
    public static <T> T convertStringToObject(String value, Class<T> type) {
        return objectMapper.readValue(value, type);
    }

    @SneakyThrows
    public static <T> T convertStringToObject(String value, TypeReference<T> typeReference) {
        return objectMapper.readValue(value, typeReference);
    }
}
