package com.example.serviceportfolio.filter

import com.example.serviceportfolio.config.RateLimitConfig
import io.github.bucket4j.Bucket
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitFilter(
    private val rateLimitConfig: RateLimitConfig,
    private val rateLimitBuckets: ConcurrentHashMap<String, Bucket>
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(javaClass)
    
    // Store bucket creation time to calculate reset time
    private val bucketResetTimes: ConcurrentHashMap<String, Long> = ConcurrentHashMap()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        
        // Only apply rate limiting to specific endpoints
        if (!shouldRateLimit(path)) {
            filterChain.doFilter(request, response)
            return
        }

        val clientIp = getClientIp(request)
        val bucketKey = "$clientIp:$path"
        
        val bucket = rateLimitBuckets.computeIfAbsent(bucketKey) { 
            val now = System.currentTimeMillis()
            bucketResetTimes[bucketKey] = now + 60000 // 1 minute from now
            createBucketForPath(path)
        }
        
        val resetTime = bucketResetTimes.getOrDefault(bucketKey, System.currentTimeMillis() + 60000)

        val probe = bucket.tryConsumeAndReturnRemaining(1)
        
        if (probe.isConsumed) {
            // Add rate limit headers
            response.addHeader("X-RateLimit-Limit", getBucketCapacity(path).toString())
            response.addHeader("X-RateLimit-Remaining", probe.remainingTokens.toString())
            response.addHeader("X-RateLimit-Reset", (resetTime / 1000).toString())
            
            filterChain.doFilter(request, response)
        } else {
            logger.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, path)
            
            // Calculate seconds until reset
            val secondsUntilReset = ((resetTime - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
            
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.addHeader("X-RateLimit-Limit", getBucketCapacity(path).toString())
            response.addHeader("X-RateLimit-Remaining", "0")
            response.addHeader("X-RateLimit-Reset", (resetTime / 1000).toString())
            response.addHeader("Retry-After", secondsUntilReset.toString())
            response.contentType = "application/json"
            response.writer.write(
                """{"error":"Rate limit exceeded","message":"Too many requests. Please try again later."}"""
            )
        }
    }

    private fun shouldRateLimit(path: String): Boolean {
        return path.startsWith("/api/v1/repos/analyze") ||
               path.startsWith("/api/v1/portfolio/generate")
    }

    private fun createBucketForPath(path: String): Bucket {
        return when {
            path.startsWith("/api/v1/repos/analyze") -> rateLimitConfig.createAnalyzeBucket()
            path.startsWith("/api/v1/portfolio/generate") -> rateLimitConfig.createPortfolioBucket()
            else -> rateLimitConfig.createDefaultBucket()
        }
    }

    private fun getBucketCapacity(path: String): Long {
        return when {
            path.startsWith("/api/v1/repos/analyze") -> RateLimitConfig.ANALYZE_REQUESTS_PER_MINUTE
            path.startsWith("/api/v1/portfolio/generate") -> RateLimitConfig.PORTFOLIO_REQUESTS_PER_MINUTE
            else -> RateLimitConfig.DEFAULT_REQUESTS_PER_MINUTE
        }
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return if (xForwardedFor.isNullOrEmpty()) {
            request.remoteAddr
        } else {
            xForwardedFor.split(",")[0].trim()
        }
    }
}
