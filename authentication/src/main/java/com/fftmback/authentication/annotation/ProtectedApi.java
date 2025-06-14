package com.fftmback.authentication.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@SecurityRequirement(name = "Bearer Authentication")
@ApiResponse(responseCode = "401", description = "Token expired", content = @Content)
@ApiResponse(
        responseCode = "403",
        description = "User has valid credentials but not enough privileges to perform an action on a resource",
        content = @Content
)
public @interface ProtectedApi {
}
