package com.febfes.fftmback.annotation;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Parameter(
        name = "taskFilter",
        description = """
                Filter parameter. You need to send an array of filters. Filter consists of:
                1. Property - property name in entity. For example: name
                2. Operator - EQUAL, NOT_EQUAL, LIKE (strings), IN (dates, numbers, strings), BETWEEN (dates and numbers)
                3. Value - value to filter. Date format (send like string): dd-MM-yyyy HH:mm:ss
                4. ValueTo - for BETWEEN operator (value=1 BETWEEN valueTo=2)
                5. Values - for IN filter (IN (1, 2, 3))
                                
                For example: taskFilter=[{"property":"name","operator":"LIKE","value":"string"}]
                """
)
public @interface FilterParam {
}
