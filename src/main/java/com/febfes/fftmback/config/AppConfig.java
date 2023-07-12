package com.febfes.fftmback.config;

import com.febfes.fftmback.domain.common.TaskPriority;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.swagger.springdoc.SpecificationArgResolverSpringdocOperationCustomizer;
import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

    final MyConversionService conversionService;

    @Bean
    public SpecificationArgResolverSpringdocOperationCustomizer specificationArgResolverSpringdocOperationCustomizer() {
        return new SpecificationArgResolverSpringdocOperationCustomizer();
    }

//    @Bean
//    @Primary
//    public ConversionService argumentResolverConversionService(
//            @Qualifier("mvcConversionService") final ConversionService conversionService
//    ) {
//        return conversionService;
//    }

//    @Bean
//    public ConversionService ConversionService() {
//        DefaultConversionService service = new DefaultConversionService();
//        //registering our custom ConverterFactory
//        service.addConverterFactory(new StringToPriorityConverter());
//        return service;
//    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
//        argumentResolvers.add(new SpecificationArgumentResolver());
        argumentResolvers.add(new SpecificationArgumentResolver(conversionService));
//        argumentResolvers.add(new SpecificationArgumentResolver(argumentResolverConversionService()));
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToPriorityConverter());
        registry.addConverter(new PriorityToStringConverter());
    }

    public static class StringToPriorityConverter implements Converter<String, TaskPriority> {
        @Override
        public TaskPriority convert(@NonNull String rawPriority) {
            return TaskPriority.valueOf(rawPriority);
        }
    }

    public static class PriorityToStringConverter implements Converter<TaskPriority, String> {
        @Override
        public String convert(@NonNull TaskPriority priority) {
            return priority.name();
        }
    }

}
