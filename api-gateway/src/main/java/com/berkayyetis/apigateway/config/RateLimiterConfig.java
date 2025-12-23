package com.berkayyetis.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");

            // Eğer X-User-ID yoksa, JWT'den userId al
            if (userId == null) {
                String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    // JWT'den user ID parse et (optional)
                    // userId = parseUserIdFromJwt(authHeader);
                }
            }
            String finalKey = userId != null ? userId : "anonymous";
            return Mono.just(finalKey);
        };
    }
}