package com.febfes.fftmback.config;

import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.stereotype.Component;

@Component
@Primary
public class MyConversionService extends DefaultConversionService implements ConversionService {
    // an empty ConversionService to initiate call to register converters
}
