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

        Claims claims;
        try {
            claims = jwtValidator.validate(authHeader.substring(7));
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // RBAC decision
        Mono<Void> rbacResult = rbacFilter.authorize(exchange, claims);

        // IMPORTANT: if RBAC completed response, STOP
        if (exchange.getResponse().isCommitted()) {
            return rbacResult;
        }

        //  Allowed → forward request
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-USER-ID", claims.getSubject())
                .header("X-ROLE", claims.get("role", String.class))
                .header("X-USER-NAME", claims.get("name", String.class))
                .header("X-USER-EMAIL", claims.get("email", String.class))
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }
}
