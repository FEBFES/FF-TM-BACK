package com.febfes.gateway.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConnValidationResponse(
        @JsonProperty("status") String status,
        @JsonProperty("isAuthenticated") boolean isAuthenticated,
        @JsonProperty("methodType") String methodType,
        @JsonProperty("username") String username,
        @JsonProperty("role") String role
) {
}
