package com.example.serviceportfolio.services

import com.example.serviceportfolio.client.GitHubAppTokenService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GitHubRateLimitServiceTest {

    private lateinit var tokenService: GitHubAppTokenService
    private lateinit var rateLimitService: GitHubRateLimitService

    @BeforeEach
    fun setUp() {
        tokenService = mock(GitHubAppTokenService::class.java)
        rateLimitService = GitHubRateLimitService(tokenService)
    }

    @Test
    fun `should return rate limit info data class correctly`() {
        // Test the data class
        val info = GitHubRateLimitService.RateLimitInfo(
            limit = 5000,
            remaining = 4500,
            reset = System.currentTimeMillis() + 3600000
        )
        
        assertEquals(5000, info.limit)
        assertEquals(4500, info.remaining)
        assertTrue(info.reset > System.currentTimeMillis())
    }

    @Test
    fun `should return false when checking availability fails`() {
        // Given
        whenever(tokenService.getInstallationToken()).thenThrow(IOException("Connection error"))

        // When
        val hasAvailable = rateLimitService.hasAvailableRequests()

        // Then
        assertFalse(hasAvailable)
    }
}
