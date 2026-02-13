package com.example.serviceportfolio.services

import com.example.serviceportfolio.client.GitHubAppTokenService
import org.kohsuke.github.GitHubBuilder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class GitHubRateLimitService(
    private val tokenService: GitHubAppTokenService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        // Log a warning when remaining requests fall below this threshold
        // This helps administrators monitor API usage before hitting the limit
        private const val RATE_LIMIT_THRESHOLD = 100
    }

    fun checkRateLimit(): RateLimitInfo {
        try {
            val gitHub = GitHubBuilder()
                .withAppInstallationToken(tokenService.getInstallationToken())
                .build()

            val rateLimit = gitHub.rateLimit()
            val core = rateLimit.core

            val info = RateLimitInfo(
                limit = core.limit,
                remaining = core.remaining,
                reset = core.reset.time
            )

            if (core.remaining < RATE_LIMIT_THRESHOLD) {
                logger.warn(
                    "GitHub API rate limit low: {}/{} remaining, resets at {}",
                    core.remaining,
                    core.limit,
                    core.reset
                )
            }

            return info

        } catch (e: IOException) {
            logger.error(
                "Failed to check GitHub API rate limit. This may be due to network issues, " +
                "invalid GitHub App token, or GitHub API unavailability. Error: ${e.message}", 
                e
            )
            throw e
        }
    }

    fun hasAvailableRequests(): Boolean {
        return try {
            val info = checkRateLimit()
            info.remaining > 0
        } catch (e: Exception) {
            logger.error(
                "Error checking GitHub API rate limit availability. " +
                "Defaulting to false to prevent potential rate limit violations. Error: ${e.message}", 
                e
            )
            false
        }
    }

    data class RateLimitInfo(
        val limit: Int,
        val remaining: Int,
        val reset: Long
    )
}
