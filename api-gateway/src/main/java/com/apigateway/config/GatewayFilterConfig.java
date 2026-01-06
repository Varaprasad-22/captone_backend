package com.apigateway.config;


import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.apigateway.security.JwtAuthenticationFilter;

@Configuration
public class GatewayFilterConfig {

    @Bean
    public GlobalFilter jwtGlobalFilter(JwtAuthenticationFilter filter) {
        return (exchange, chain) -> filter.filter(exchange, chain);
    }
}
