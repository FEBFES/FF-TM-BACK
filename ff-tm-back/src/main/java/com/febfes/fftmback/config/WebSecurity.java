package com.febfes.fftmback.config;

import com.febfes.fftmback.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurity {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
                .csrf()
                .disable()
                .cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/v1/auth/register",
                        "/v1/auth/authenticate",
                        "/v1/auth/refresh-token",
                        "/v1/auth/check-token-expiration",
                        "/v1/roles",
                        // TODO: remove unnecessary
                        "/swagger-ui/**",
                        "/v1/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v1/v3/api-docs/**"
                )
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/v1/files/user-pic/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
