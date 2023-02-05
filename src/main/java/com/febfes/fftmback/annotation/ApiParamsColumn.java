package com.febfes.fftmback.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Parameter(in = ParameterIn.PATH, name ="projectId" ,schema = @Schema(type = "number", description = "Project id"))
@Parameter(in = ParameterIn.PATH, name ="columnId" ,schema = @Schema(type = "number", description = "Column id"))
public @interface ApiParamsColumn {
}
