package com.example.serviceportfolio.filter

import com.example.serviceportfolio.config.RateLimitConfig
import io.github.bucket4j.Bucket
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus
import java.io.PrintWriter
import java.util.concurrent.ConcurrentHashMap
import kotlin.test.assertEquals

class RateLimitFilterTest {

    private lateinit var rateLimitConfig: RateLimitConfig
    private lateinit var rateLimitBuckets: ConcurrentHashMap<String, Bucket>
    private lateinit var rateLimitFilter: RateLimitFilter
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse
    private lateinit var filterChain: FilterChain
    private lateinit var writer: PrintWriter

    @BeforeEach
    fun setUp() {
        rateLimitConfig = RateLimitConfig()
        rateLimitBuckets = ConcurrentHashMap()
        rateLimitFilter = RateLimitFilter(rateLimitConfig, rateLimitBuckets)
        request = mock(HttpServletRequest::class.java)
        response = mock(HttpServletResponse::class.java)
        filterChain = mock(FilterChain::class.java)
        writer = mock(PrintWriter::class.java)
        
        `when`(response.writer).thenReturn(writer)
        `when`(request.remoteAddr).thenReturn("127.0.0.1")
    }

    @Test
    fun `should allow request when rate limit not exceeded for analyze endpoint`() {
        // Given
        `when`(request.requestURI).thenReturn("/api/v1/repos/analyze")

        // When
        rateLimitFilter.doFilterInternal(request, response, filterChain)

        // Then
        verify(filterChain).doFilter(request, response)
        verify(response).addHeader(eq("X-RateLimit-Limit"), eq("10"))
        verify(response).addHeader(eq("X-RateLimit-Remaining"), any())
    }

    @Test
    fun `should allow request when rate limit not exceeded for portfolio endpoint`() {
        // Given
        `when`(request.requestURI).thenReturn("/api/v1/portfolio/generate")

        // When
        rateLimitFilter.doFilterInternal(request, response, filterChain)

        // Then
        verify(filterChain).doFilter(request, response)
        verify(response).addHeader(eq("X-RateLimit-Limit"), eq("5"))
        verify(response).addHeader(eq("X-RateLimit-Remaining"), any())
    }

    @Test
    fun `should block request when rate limit exceeded for analyze endpoint`() {
        // Given
        `when`(request.requestURI).thenReturn("/api/v1/repos/analyze")
        
        // Exhaust the rate limit (10 requests per minute)
        repeat(10) {
            rateLimitFilter.doFilterInternal(request, response, filterChain)
        }
        
        // Reset mocks for the final request
        reset(response, filterChain, writer)
        `when`(response.writer).thenReturn(writer)

        // When - 11th request should be blocked
        rateLimitFilter.doFilterInternal(request, response, filterChain)

        // Then
        verify(response).status = HttpStatus.TOO_MANY_REQUESTS.value()
        verify(response).addHeader("X-RateLimit-Limit", "10")
        verify(response).addHeader("X-RateLimit-Remaining", "0")
        verify(response).addHeader(eq("Retry-After"), any())
        verify(filterChain, never()).doFilter(any(), any())
    }

    @Test
    fun `should not apply rate limiting to non-rate-limited endpoints`() {
        // Given
        `when`(request.requestURI).thenReturn("/api/v1/portfolio/1")

        // When
        rateLimitFilter.doFilterInternal(request, response, filterChain)

        // Then
        verify(filterChain).doFilter(request, response)
        verify(response, never()).addHeader(eq("X-RateLimit-Limit"), any())
    }

    @Test
    fun `should use X-Forwarded-For header for client IP if present`() {
        // Given
        `when`(request.requestURI).thenReturn("/api/v1/repos/analyze")
        `when`(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1")

        // When
        rateLimitFilter.doFilterInternal(request, response, filterChain)

        // Then
        verify(filterChain).doFilter(request, response)
        // Verify that a bucket was created with the forwarded IP
        assertEquals(1, rateLimitBuckets.size)
    }
}
