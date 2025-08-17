package com.febfes.fftmback.config.feign;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "feign.okhttp")
@Setter
@Getter
public class FeignOkHttpProperties {

    private int maxIdleConnections;
    private long keepAliveDurationMinutes;
    private long connectTimeoutMillis;
    private long readTimeoutSeconds;
    private long writeTimeoutSeconds;
    private boolean retryOnConnectionFailure;
}
