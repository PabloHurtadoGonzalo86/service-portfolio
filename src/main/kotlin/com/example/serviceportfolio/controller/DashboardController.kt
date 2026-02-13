package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.*
import com.example.serviceportfolio.models.DeveloperPortfolio
import com.example.serviceportfolio.repositories.AnalysisResultRepository
import com.example.serviceportfolio.repositories.PortfolioRepository
import com.example.serviceportfolio.services.AuthenticationService
import tools.jackson.databind.ObjectMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "User dashboard with portfolio and analysis management")
class DashboardController(
    private val authenticationService: AuthenticationService,
    private val portfolioRepository: PortfolioRepository,
    private val analysisResultRepository: AnalysisResultRepository,
    private val objectMapper: ObjectMapper
) {

    @Operation(summary = "Get user dashboard", description = "Returns authenticated user's portfolios, analyses, and statistics")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @GetMapping
    fun getDashboard(): ResponseEntity<DashboardResponse> {
        val user = authenticationService.requireCurrentUser()

        val portfolios = portfolioRepository.findAllByUserOrderByCreatedAtDesc(user)
        val analyses = analysisResultRepository.findAllByUserOrderByCreatedAtDesc(user)

        val userInfo = UserInfo(
            githubUsername = user.githubUsername,
            name = user.name,
            email = user.email,
            avatarUrl = user.avatarUrl,
            memberSince = user.createdAt
        )

        val stats = DashboardStats(
            totalPortfolios = portfolios.size,
            totalAnalyses = analyses.size
        )

        val recentPortfolios = portfolios.take(5).map { entity ->
            val portfolio = objectMapper.readValue(entity.portfolioData, DeveloperPortfolio::class.java)
            PortfolioSummaryResponse(
                id = entity.id,
                githubUsername = entity.githubUsername,
                developerName = portfolio.developerName,
                professionalSummary = portfolio.professionalSummary,
                topSkills = portfolio.topSkills,
                totalPublicRepos = entity.totalPublicRepos,
                projectCount = portfolio.selectedProjects.size,
                createdAt = entity.createdAt
            )
        }

        val recentAnalyses = analyses.take(5).map { entity ->
            AnalysisSummaryResponse(
                id = entity.id,
                projectName = entity.projectName,
                repoUrl = entity.repoUrl,
                shortDescription = entity.shortDescription,
                techStack = entity.techStack,
                createdAt = entity.createdAt
            )
        }

        val response = DashboardResponse(
            user = userInfo,
            stats = stats,
            recentPortfolios = recentPortfolios,
            recentAnalyses = recentAnalyses
        )

        return ResponseEntity.ok(response)
    }
}
