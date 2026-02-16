package com.example.serviceportfolio.services

import com.example.serviceportfolio.entities.User
import com.example.serviceportfolio.exceptions.UsageLimitExceededException
import com.example.serviceportfolio.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset

@Service
class UsageLimitService(
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        val PLAN_LIMITS = mapOf(
            "FREE" to PlanLimits(analyses = 5, portfolios = 3),
            "PRO" to PlanLimits(analyses = 50, portfolios = 20)
        )
    }

    data class PlanLimits(val analyses: Int, val portfolios: Int)

    @Transactional
    fun checkAndIncrementAnalysis(user: User) {
        resetIfNeeded(user)
        val limits = PLAN_LIMITS[user.plan] ?: PLAN_LIMITS["FREE"]!!
        if (user.analysesUsed >= limits.analyses) {
            throw UsageLimitExceededException(
                "Monthly analysis limit reached (${user.analysesUsed}/${limits.analyses}). " +
                "Resets at ${user.usageResetAt}."
            )
        }
        user.analysesUsed++
        userRepository.save(user)
        logger.debug("Analysis usage incremented for user {}: {}", user.githubUsername, user.analysesUsed)
    }

    @Transactional
    fun checkAndIncrementPortfolio(user: User) {
        resetIfNeeded(user)
        val limits = PLAN_LIMITS[user.plan] ?: PLAN_LIMITS["FREE"]!!
        if (user.portfoliosUsed >= limits.portfolios) {
            throw UsageLimitExceededException(
                "Monthly portfolio limit reached (${user.portfoliosUsed}/${limits.portfolios}). " +
                "Resets at ${user.usageResetAt}."
            )
        }
        user.portfoliosUsed++
        userRepository.save(user)
        logger.debug("Portfolio usage incremented for user {}: {}", user.githubUsername, user.portfoliosUsed)
    }

    fun checkAnalysisLimit(user: User) {
        resetIfNeeded(user)
        val limits = PLAN_LIMITS[user.plan] ?: PLAN_LIMITS["FREE"]!!
        if (user.analysesUsed >= limits.analyses) {
            throw UsageLimitExceededException(
                "Monthly analysis limit reached (${user.analysesUsed}/${limits.analyses}). " +
                "Resets at ${user.usageResetAt}."
            )
        }
    }

    fun checkPortfolioLimit(user: User) {
        resetIfNeeded(user)
        val limits = PLAN_LIMITS[user.plan] ?: PLAN_LIMITS["FREE"]!!
        if (user.portfoliosUsed >= limits.portfolios) {
            throw UsageLimitExceededException(
                "Monthly portfolio limit reached (${user.portfoliosUsed}/${limits.portfolios}). " +
                "Resets at ${user.usageResetAt}."
            )
        }
    }

    private fun resetIfNeeded(user: User) {
        if (Instant.now().isAfter(user.usageResetAt)) {
            logger.info("Resetting usage counters for user {}", user.githubUsername)
            user.analysesUsed = 0
            user.portfoliosUsed = 0
            user.usageResetAt = calculateNextResetDate()
            userRepository.save(user)
        }
    }

    private fun calculateNextResetDate(): Instant {
        val nextMonth = YearMonth.now(ZoneOffset.UTC).plusMonths(1)
        return nextMonth.atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC)
    }
}
