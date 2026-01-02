package com.apiGateway.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {


    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtValidator jwtValidator;
    private final RoleBasedAuthorizationFilter rbacFilter;

    public JwtAuthenticationFilter(
            JwtValidator jwtValidator,
            RoleBasedAuthorizationFilter rbacFilter) {
        this.jwtValidator = jwtValidator;
        this.rbacFilter = rbacFilter;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        log.info(" Incoming request: {} {}", 
                exchange.getRequest().getMethod(), path);
        
        // Skip the login and register routes
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            log.info(" Public endpoint, skipping JWT check");
            return chain.filter(exchange);
        }

        // Extract the Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");
        log.info(" Authorization header = {}", authHeader);
        // If there's no Authorization header or it's not a Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn(" Missing or invalid Authorization header");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            // Validate the JWT token and extract claims
            Claims claims = jwtValidator.validate(authHeader.substring(7));
            log.info("JWT validated | userId={} | role={}",
                    claims.getSubject(),
                    claims.get("role", String.class));

            // Add the headers for User ID, Role, Name, and Email
            return rbacFilter.authorize(exchange, claims, chain)
                    .then(chain.filter(
                        exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                .header("X-USER-ID", claims.getSubject())  // User ID
                                .header("X-ROLE", claims.get("role", String.class))  // Role
                                .header("X-USER-NAME", claims.get("name", String.class))  // User Name
                                .header("X-USER-EMAIL", claims.get("email", String.class))  // User Email
                                .build())
                            .build()
                    ));

        } catch (Exception e) {
            log.error("❌ JWT validation failed", e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}
