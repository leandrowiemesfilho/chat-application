package com.chatapp.apigateway.filter;

import com.chatapp.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtService jwtService;

    @Autowired
    public JwtAuthenticationFilter(final JwtService jwtService) {
        super(Config.class);

        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            final ServerHttpRequest request = exchange.getRequest();
            final String path = request.getPath().toString();

            // Skip JWT validation for public endpoints
            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }

            // Get JWT token from header
            final List<String> authHeaders = request.getHeaders().get(HttpHeaders.AUTHORIZATION);
            if (authHeaders == null || authHeaders.isEmpty() ||
                    !authHeaders.getFirst().startsWith("Bearer ")) {
                return onError(exchange, "Missing or invalid Authorization header");
            }

            String token = authHeaders.getFirst().substring(7);

            try {
                // Validate JWT token
                final Claims claims = this.jwtService.validateToken(token);

                // Add user information to headers for downstream services
                final String userId = claims.get("userId", String.class);
                final String phoneNumber = claims.getSubject();
                final String email = claims.get("email", String.class);
                final String name = claims.get("name", String.class);
                final String sessionToken = getSessionTokenFromClaims(claims);

                final ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-ID", userId)
                        .header("X-User-Phone", phoneNumber)
                        .header("X-User-Email", email)
                        .header("X-User-Name", name)
                        .header("X-Session-Token", sessionToken)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (JwtException e) {
                return onError(exchange, "Invalid JWT token: " + e.getMessage());
            }
        };
    }

    private static boolean isPublicEndpoint(final String path) {
        return path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/password/reset/request");
    }

    private static String getSessionTokenFromClaims(final Claims claims) {
        // Extract session token from JWT claims if needed
        return claims.get("sessionToken", String.class);
    }

    private static Mono<Void> onError(final ServerWebExchange exchange, final String error) {
        final ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        final String errorBody = String.format("{\"status\": %d, \"error\": \"%s\"}",
                HttpStatus.UNAUTHORIZED.value(),
                error);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }

    public static class Config {
        // Configuration properties
    }
}
