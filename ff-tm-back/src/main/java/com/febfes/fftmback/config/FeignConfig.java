package com.febfes.fftmback.config;

import feign.Logger;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.febfes.fftmback.config.jwt.JwtAuthenticationFilter.BEARER;

@Configuration
public class FeignConfig {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = getBearerTokenFromCurrentRequest();
            if (token != null) {
                requestTemplate.header(AUTHORIZATION_HEADER, token);
            }
        };
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    private String getBearerTokenFromCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        return (authHeader != null && authHeader.startsWith(BEARER)) ? authHeader : null;
    }
}
