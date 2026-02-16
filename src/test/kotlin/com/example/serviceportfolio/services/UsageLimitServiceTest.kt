package com.example.serviceportfolio.services

import com.example.serviceportfolio.entities.User
import com.example.serviceportfolio.exceptions.UsageLimitExceededException
import com.example.serviceportfolio.repositories.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneOffset

class UsageLimitServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var usageLimitService: UsageLimitService

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        usageLimitService = UsageLimitService(userRepository)
    }

    private fun createUser(
        plan: String = "FREE",
        analysesUsed: Int = 0,
        portfoliosUsed: Int = 0,
        usageResetAt: Instant = Instant.now().plusSeconds(86400)
    ): User {
        return User(
            githubId = 12345L,
            githubUsername = "testuser"
        ).apply {
            this.plan = plan
            this.analysesUsed = analysesUsed
            this.portfoliosUsed = portfoliosUsed
            this.usageResetAt = usageResetAt
        }
    }

    // --- checkAnalysisLimit ---

    @Test
    fun `checkAnalysisLimit does not throw when under limit`() {
        val user = createUser(analysesUsed = 3)
        usageLimitService.checkAnalysisLimit(user)
    }

    @Test
    fun `checkAnalysisLimit throws when at limit`() {
        val user = createUser(analysesUsed = 5)
        assertThrows(UsageLimitExceededException::class.java) {
            usageLimitService.checkAnalysisLimit(user)
        }
    }

    @Test
    fun `checkAnalysisLimit uses PRO limits for PRO plan`() {
        val user = createUser(plan = "PRO", analysesUsed = 10)
        usageLimitService.checkAnalysisLimit(user)
    }

    @Test
    fun `checkAnalysisLimit throws for PRO plan at limit`() {
        val user = createUser(plan = "PRO", analysesUsed = 50)
        assertThrows(UsageLimitExceededException::class.java) {
            usageLimitService.checkAnalysisLimit(user)
        }
    }

    // --- checkPortfolioLimit ---

    @Test
    fun `checkPortfolioLimit does not throw when under limit`() {
        val user = createUser(portfoliosUsed = 1)
        usageLimitService.checkPortfolioLimit(user)
    }

    @Test
    fun `checkPortfolioLimit throws when at limit`() {
        val user = createUser(portfoliosUsed = 3)
        assertThrows(UsageLimitExceededException::class.java) {
            usageLimitService.checkPortfolioLimit(user)
        }
    }

    // --- checkAndIncrementAnalysis ---

    @Test
    fun `checkAndIncrementAnalysis increments counter and saves`() {
        val user = createUser(analysesUsed = 2)
        `when`(userRepository.save(any<User>())).thenReturn(user)

        usageLimitService.checkAndIncrementAnalysis(user)

        assertEquals(3, user.analysesUsed)
        verify(userRepository).save(user)
    }

    @Test
    fun `checkAndIncrementAnalysis throws when at limit`() {
        val user = createUser(analysesUsed = 5)
        assertThrows(UsageLimitExceededException::class.java) {
            usageLimitService.checkAndIncrementAnalysis(user)
        }
    }

    // --- checkAndIncrementPortfolio ---

    @Test
    fun `checkAndIncrementPortfolio increments counter and saves`() {
        val user = createUser(portfoliosUsed = 1)
        `when`(userRepository.save(any<User>())).thenReturn(user)

        usageLimitService.checkAndIncrementPortfolio(user)

        assertEquals(2, user.portfoliosUsed)
        verify(userRepository).save(user)
    }

    @Test
    fun `checkAndIncrementPortfolio throws when at limit`() {
        val user = createUser(portfoliosUsed = 3)
        assertThrows(UsageLimitExceededException::class.java) {
            usageLimitService.checkAndIncrementPortfolio(user)
        }
    }

    // --- resetIfNeeded ---

    @Test
    fun `checkAnalysisLimit resets counters when reset date has passed`() {
        val user = createUser(
            analysesUsed = 5,
            portfoliosUsed = 3,
            usageResetAt = Instant.now().minusSeconds(86400)
        )
        `when`(userRepository.save(any<User>())).thenReturn(user)

        usageLimitService.checkAnalysisLimit(user)

        assertEquals(0, user.analysesUsed)
        assertEquals(0, user.portfoliosUsed)

        val expectedResetDate = YearMonth.now(ZoneOffset.UTC).plusMonths(1)
            .atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC)
        assertEquals(expectedResetDate, user.usageResetAt)
        verify(userRepository).save(user)
    }

    @Test
    fun `checkAnalysisLimit falls back to FREE limits for unknown plan`() {
        val user = createUser(plan = "UNKNOWN", analysesUsed = 5)
        assertThrows(UsageLimitExceededException::class.java) {
            usageLimitService.checkAnalysisLimit(user)
        }
    }
}
