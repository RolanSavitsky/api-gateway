package com.inno.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final String BEARER_PREFIX = "Bearer ";

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/refresh"
    );

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (PUBLIC_PATHS.contains(path)) {
            return chain.filter(exchange);
        }

        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange);
        }

        String token = header.substring(BEARER_PREFIX.length());

        try {
            Claims claims = jwtService.parse(token);

            if (!"access".equals(claims.get("type", String.class))) {
                return unauthorized(exchange);
            }

        } catch (JwtException | IllegalArgumentException exception) {
            return unauthorized(exchange);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Authentication required\",\"timestamp\":\"%s\"}",
                LocalDateTime.now());

        DataBuffer buffer = response.bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
