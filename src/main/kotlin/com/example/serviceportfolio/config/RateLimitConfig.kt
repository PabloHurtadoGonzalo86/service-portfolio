package com.example.serviceportfolio.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Configuration
class RateLimitConfig {

    companion object {
        // Rate limits per IP address
        const val ANALYZE_REQUESTS_PER_MINUTE = 10L
        const val PORTFOLIO_REQUESTS_PER_MINUTE = 5L
        const val DEFAULT_REQUESTS_PER_MINUTE = 60L
    }

    @Bean
    fun rateLimitBuckets(): ConcurrentHashMap<String, Bucket> {
        return ConcurrentHashMap()
    }

    fun createAnalyzeBucket(): Bucket {
        return Bucket.builder()
            .addLimit(
                Bandwidth.classic(
                    ANALYZE_REQUESTS_PER_MINUTE,
                    Refill.intervally(ANALYZE_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
                )
            )
            .build()
    }

    fun createPortfolioBucket(): Bucket {
        return Bucket.builder()
            .addLimit(
                Bandwidth.classic(
                    PORTFOLIO_REQUESTS_PER_MINUTE,
                    Refill.intervally(PORTFOLIO_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
                )
            )
            .build()
    }

    fun createDefaultBucket(): Bucket {
        return Bucket.builder()
            .addLimit(
                Bandwidth.classic(
                    DEFAULT_REQUESTS_PER_MINUTE,
                    Refill.intervally(DEFAULT_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
                )
            )
            .build()
    }
}
