package com.fftmback.annotation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@ApiGet
@ApiResponse(responseCode = "404", description = "Not found", content = @Content)
public @interface ApiGetOne {

    @AliasFor(annotation = ApiGet.class)
    String[] value() default {};

    @AliasFor(annotation = ApiGet.class)
    String[] path() default {};
}
