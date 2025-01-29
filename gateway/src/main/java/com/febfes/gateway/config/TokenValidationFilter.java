package com.febfes.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.febfes.gateway.ConnValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
//public class TokenValidationFilter implements GatewayFilter {
public class TokenValidationFilter extends AbstractGatewayFilterFactory<TokenValidationFilter.Config> {

    private final List<String> excludedUrls;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;


//    public TokenValidationFilter(WebClient.Builder webClientBuilder) {
//        this.webClientBuilder = webClientBuilder;
//    }

    @Autowired
    public TokenValidationFilter(
            @Qualifier("excludedUrls") List<String> excludedUrls,
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper
    ) {
        super(Config.class);
        this.excludedUrls = excludedUrls;
        this.webClientBuilder = webClientBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (isSecured(exchange)) {
                return validateToken(exchange, chain);
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> validateToken(ServerWebExchange exchange, GatewayFilterChain chain) {
        String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8092/api/v1/auth/validate-token")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .header("X-init-uri", exchange.getRequest().getURI().getPath())
                .retrieve()
//                .bodyToMono(String.class) //TODO: try ConnValidationResponse.class
                .bodyToMono(ConnValidationResponse.class)
                .flatMap(response -> {
                    exchange.getRequest().mutate()
                            .header("X-username", response.username())
                            .header("X-user-role", response.role());
                    return chain.filter(exchange);
                })
//                .flatMap(json -> processValidationResponse(json, exchange, chain))
                .onErrorResume(error -> handleValidationError(exchange));
    }

    private Mono<Void> processValidationResponse(String json, ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            ConnValidationResponse response = objectMapper.readValue(json, ConnValidationResponse.class);
            exchange.getRequest().mutate()
                    .header("X-username", response.username())
                    .header("X-user-role", response.role());
            return chain.filter(exchange);
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException(e));
        }
    }

    private Mono<Void> handleValidationError(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            String bearerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//            if (isSecured(exchange)) {
//                if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
//                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                    return exchange.getResponse().setComplete();
//                }
//                return webClientBuilder.build()
//                        .get()
//                        .uri("http://localhost:8092/api/v1/auth/validate-token")
//                        .header(HttpHeaders.AUTHORIZATION, bearerToken)
//                        .header("X-init-uri", exchange.getRequest().getURI().getPath())
//                        .retrieve()
//                        .bodyToMono(String.class)
//                        .flatMap(json -> {
//                            ObjectMapper objectMapper = new ObjectMapper();
//                            ConnValidationResponse response;
//                            try {
//                                response = objectMapper.readValue(json, ConnValidationResponse.class);
//                                exchange.getRequest().mutate()
//                                        .header("X-username", response.username())
//                                        .header("X-user-role", response.role());
//                                return chain.filter(exchange);
//                            } catch (JsonProcessingException e) {
//                                return Mono.error(new RuntimeException(e));
//                            }
//                        })
//                        .onErrorResume(error -> {
//                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                            return exchange.getResponse().setComplete();
//                        });
//            }
//
//            return chain.filter(exchange);
//        };
//    }

    public boolean isSecured(ServerWebExchange exchange) {
        String requestPath = exchange.getRequest().getURI().getPath();
        return excludedUrls.stream().noneMatch(requestPath::contains);
    }

    public static class Config {
    }
}
