package com.example.serviceportfolio.config

import org.springframework.context.annotation.Configuration

@Configuration
class RateLimitConfig {

    data class RateLimitRule(
        val pathPattern: String,
        val capacity: Long,
        val refillTokens: Long,
        val refillPeriodMinutes: Long
    )

    companion object {
        val RATE_LIMIT_RULES = listOf(
            RateLimitRule("/api/v1/repos/analyze", capacity = 5, refillTokens = 5, refillPeriodMinutes = 60),
            RateLimitRule("/api/v1/portfolio/generate", capacity = 3, refillTokens = 3, refillPeriodMinutes = 60),
            RateLimitRule("/api/v1/repos/readme/commit", capacity = 10, refillTokens = 10, refillPeriodMinutes = 60),
            RateLimitRule("/api/v1/**", capacity = 60, refillTokens = 60, refillPeriodMinutes = 1)
        )

        val EXEMPT_PATHS = setOf(
            "/actuator/**",
            "/swagger-ui/**",
            "/api-docs/**",
            "/"
        )
    }
}
