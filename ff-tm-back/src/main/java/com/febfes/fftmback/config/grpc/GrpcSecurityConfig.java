package com.febfes.fftmback.config.grpc;

import com.febfes.fftmback.config.jwt.JwtAuthConverter;
import net.devh.boot.grpc.server.security.authentication.BearerAuthenticationReader;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcSecurityConfig {

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader(JwtAuthConverter jwtAuthConverter) {
        // BearerAuthenticationReader читает токен из метадаты "authorization"
        return new BearerAuthenticationReader(jwtAuthConverter::convert);
    }
}
