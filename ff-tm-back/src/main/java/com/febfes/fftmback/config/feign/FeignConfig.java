package com.febfes.fftmback.config.feign;

import feign.Client;
import feign.Logger;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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

    @Bean
    okhttp3.ConnectionPool feignConnectionPool() {
        // TODO: добавить все это в config-server в .yml файл в виде проперти
        return new okhttp3.ConnectionPool(200, 5, TimeUnit.MINUTES);
    }

    @Bean
    okhttp3.OkHttpClient okHttpClient(okhttp3.ConnectionPool pool) {
        return new okhttp3.OkHttpClient.Builder()
                .connectionPool(pool)
                .retryOnConnectionFailure(true)
                .connectTimeout(Duration.ofMillis(500))
                .readTimeout(Duration.ofSeconds(2))
                .writeTimeout(Duration.ofSeconds(2))
                .build();
    }

    @Bean
    Client feignClient(okhttp3.OkHttpClient client) {
        return new feign.okhttp.OkHttpClient(client);
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
