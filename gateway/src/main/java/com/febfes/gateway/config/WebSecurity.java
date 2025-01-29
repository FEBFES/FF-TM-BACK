//package com.febfes.gateway.config;
//
//import com.febfes.gateway.config.jwt.JwtAuthenticationEntryPoint;
//import com.febfes.gateway.config.jwt.JwtAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//public class WebSecurity {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//
//    public WebSecurity(
//            JwtAuthenticationFilter jwtAuthenticationFilter,
//            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
//    ) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
//    }
//
////    @Bean
////    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
////        return config.getAuthenticationManager();
////    }
//
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class)
////                .csrf()
////                .disable()
////                .cors()
////                .configurationSource(corsConfigurationSource())
////                .and()
////                .authorizeHttpRequests()
////                .requestMatchers(
////                        "/v1/notifications/swagger-ui/**",
////                        "/v1/notifications/v3/api-docs/**"
////                )
////                .permitAll()
////                .anyRequest()
////                .authenticated()
////                .and()
////                .sessionManagement()
////                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
////                .and()
////                .authenticationProvider(authenticationProvider)
////                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
//
//    @Bean
//    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .exceptionHandling(e -> e.authenticationEntryPoint(jwtAuthenticationEntryPoint))
//                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
//                        .pathMatchers(
//                                "/v1/auth/register",
//                                "/v1/auth/authenticate",
//                                "/v1/auth/refresh-token",
//                                "/v1/auth/check-token-expiration",
//                                "/v1/roles",
//                                // TODO: remove unnecessary
//                                "/swagger-ui/**",
//                                "/v1/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/v1/v3/api-docs/**"
//                        ).permitAll()
//                        .pathMatchers(HttpMethod.GET, "/v1/files/user-pic/**").permitAll()
//                        .anyExchange().authenticated())
//                .addFilterBefore(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
////                .httpBasic(HttpBasicSpec::disable)
////                .formLogin(FormLoginSpec::disable)
//                .build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        corsConfiguration.addAllowedOriginPattern("*");
//        corsConfiguration.addAllowedHeader("*");
//        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
//        corsConfiguration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", corsConfiguration);
//        return source;
//    }
//}
