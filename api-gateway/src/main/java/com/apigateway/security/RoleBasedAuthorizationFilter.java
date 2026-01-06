package com.apigateway.security;


import static com.apigateway.security.Roles.*;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

import org.springframework.web.server.ServerWebExchange;
@Component
public class RoleBasedAuthorizationFilter {

    private static final AntPathMatcher matcher = new AntPathMatcher();
    private static final Map<String, List<String>> ROLE_RULES = Map.ofEntries(

        // AUTH
        Map.entry("POST:/auth/admin/register", List.of(ADMIN)),
        Map.entry("GET:/auth/getAll", List.of(ADMIN)),
        Map.entry("PUT:/auth/deactivate", List.of(ADMIN)),
        Map.entry("PUT:/auth/users/*", List.of(ADMIN)),

        // TICKETS
        Map.entry("POST:/tickets/create", List.of(USER)),
        Map.entry("PUT:/tickets/*/status", List.of(ADMIN, MANAGER, AGENT)),
        Map.entry("GET:/tickets/getAllOpenTickets", List.of(ADMIN, MANAGER)),
        Map.entry("GET:/tickets/*/getTickets", List.of(USER, ADMIN, MANAGER)),
        Map.entry("GET:/tickets/getAllTickets", List.of(ADMIN,MANAGER)),
        Map.entry("GET:/tickets/attachments/view/*", List.of(USER, ADMIN, MANAGER,AGENT)),

        Map.entry("GET: /tickets/*/getAgentTickets", List.of(ADMIN, MANAGER,AGENT)),
        // ASSIGNMENTS
        Map.entry("POST:/assignments/assign", List.of(MANAGER)),
        Map.entry("GET:/assignments/agents/*/workload", List.of(ADMIN, MANAGER, AGENT)),
        Map.entry("GET:/assignments/manager/workload", List.of(ADMIN, MANAGER)),
        Map.entry("POST:/assignments/reassign", List.of(MANAGER,ADMIN)),
        
        // SLA EVENTS
        Map.entry("GET:/sla-events", List.of(ADMIN, MANAGER))
    );

    public Mono<Void> authorize(ServerWebExchange exchange, Claims claims) {

        String role = claims.get("role", String.class);
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();

        for (Map.Entry<String, List<String>> rule : ROLE_RULES.entrySet()) {

            String[] parts = rule.getKey().split(":");
            HttpMethod ruleMethod = HttpMethod.valueOf(parts[0]);
            String pattern = parts[1];

            if (method.equals(ruleMethod.name()) && matcher.match(pattern, path)) {

                if (!rule.getValue().contains(role)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }

                return Mono.empty(); // allowed
            }
        }

        return Mono.empty(); // open endpoint
    }

}
