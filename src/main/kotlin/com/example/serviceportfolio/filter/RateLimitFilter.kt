package com.example.serviceportfolio.filter

import com.example.serviceportfolio.config.RateLimitConfig
import com.example.serviceportfolio.exceptions.RateLimitExceededException
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration

@Component
@Order(1)
class RateLimitFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)
    private val pathMatcher = AntPathMatcher()

    private val bucketCache: Cache<String, Bucket> = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterAccess(Duration.ofHours(2))
        .build()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        if (isExemptPath(path)) {
            filterChain.doFilter(request, response)
            return
        }

        val rule = findApplicableRule(path)
        if (rule == null) {
            filterChain.doFilter(request, response)
            return
        }

        val clientIp = getClientIp(request)
        val bucketKey = "$clientIp:${rule.pathPattern}"
        val bucket = bucketCache.get(bucketKey) { createBucket(rule) }!!

        val probe = bucket.tryConsumeAndReturnRemaining(1)

        if (probe.isConsumed) {
            response.setHeader("X-RateLimit-Limit", rule.capacity.toString())
            response.setHeader("X-RateLimit-Remaining", probe.remainingTokens.toString())
            filterChain.doFilter(request, response)
        } else {
            val waitSeconds = probe.nanosToWaitForRefill / 1_000_000_000
            log.warn("Rate limit exceeded for IP {} on path {}", clientIp, path)
            response.setHeader("X-RateLimit-Limit", rule.capacity.toString())
            response.setHeader("X-RateLimit-Remaining", "0")
            response.setHeader("Retry-After", waitSeconds.toString())
            throw RateLimitExceededException("Rate limit exceeded. Retry after $waitSeconds seconds")
        }
    }

    private fun isExemptPath(path: String): Boolean =
        RateLimitConfig.EXEMPT_PATHS.any { pathMatcher.match(it, path) }

    private fun findApplicableRule(path: String): RateLimitConfig.RateLimitRule? =
        RateLimitConfig.RATE_LIMIT_RULES.firstOrNull { pathMatcher.match(it.pathPattern, path) }

    private fun createBucket(rule: RateLimitConfig.RateLimitRule): Bucket {
        val bandwidth = Bandwidth.builder()
            .capacity(rule.capacity)
            .refillIntervally(rule.refillTokens, Duration.ofMinutes(rule.refillPeriodMinutes))
            .build()
        return Bucket.builder()
            .addLimit(bandwidth)
            .build()
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (xForwardedFor != null) {
            return xForwardedFor.split(",").first().trim()
        }
        val xRealIp = request.getHeader("X-Real-IP")
        if (xRealIp != null) {
            return xRealIp
        }
        return request.remoteAddr
    }
}
