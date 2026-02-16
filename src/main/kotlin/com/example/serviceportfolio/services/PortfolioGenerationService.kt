package com.example.serviceportfolio.services

import com.example.serviceportfolio.dtos.PortfolioResponse
import com.example.serviceportfolio.dtos.PortfolioSummaryResponse
import com.example.serviceportfolio.entities.Portfolio
import com.example.serviceportfolio.entities.User
import com.example.serviceportfolio.exceptions.RepoNotFoundException
import com.example.serviceportfolio.models.DeveloperPortfolio
import com.example.serviceportfolio.repositories.PortfolioRepository
import tools.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PortfolioGenerationService(
    private val gitHubRepoService: GitHubRepoService,
    private val aiAnalysisService: AiAnalysisService,
    private val portfolioRepository: PortfolioRepository,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun generate(githubUsername: String, user: User? = null): PortfolioResponse {
        logger.info("Generating portfolio for: {}", githubUsername)

        val existing = portfolioRepository.findFirstByGithubUsernameOrderByCreatedAtDesc(githubUsername)
        if (existing.isPresent) {
            logger.info("Portfolio existente encontrado para: {}, id: {}", githubUsername, existing.get().id)
            val portfolio = objectMapper.readValue(existing.get().portfolioData, DeveloperPortfolio::class.java)
            return toResponse(existing.get(), portfolio)
        }

        val repos = gitHubRepoService.listUserRepos(githubUsername)
        if (repos.isEmpty()) {
            throw RepoNotFoundException("No public repositories found for user: $githubUsername")
        }

        val portfolio = aiAnalysisService.generatePortfolio(githubUsername, repos)

        val entity = Portfolio(
            githubUsername = githubUsername,
            portfolioData = objectMapper.writeValueAsString(portfolio),
            totalPublicRepos = repos.size
        ).apply { this.user = user }
        val saved = portfolioRepository.save(entity)

        logger.info("Portfolio saved with id: {} for user: {}", saved.id, githubUsername)
        return toResponse(saved, portfolio)
    }

    fun getById(id: Long): PortfolioResponse {
        val entity = portfolioRepository.findById(id)
            .orElseThrow { RepoNotFoundException("Portfolio not found with id: $id") }
        val portfolio = objectMapper.readValue(entity.portfolioData, DeveloperPortfolio::class.java)
        return toResponse(entity, portfolio)
    }

    fun listByUser(user: User): List<PortfolioSummaryResponse> {
        return portfolioRepository.findAllByUserOrderByCreatedAtDesc(user).map { entity ->
            val portfolio = objectMapper.readValue(entity.portfolioData, DeveloperPortfolio::class.java)
            toSummaryResponse(entity, portfolio)
        }
    }

    fun listAll(): List<PortfolioSummaryResponse> {
        return portfolioRepository.findAllByOrderByCreatedAtDesc().map { entity ->
            val portfolio = objectMapper.readValue(entity.portfolioData, DeveloperPortfolio::class.java)
            toSummaryResponse(entity, portfolio)
        }
    }

    private fun toResponse(entity: Portfolio, portfolio: DeveloperPortfolio) = PortfolioResponse(
        id = entity.id,
        githubUsername = entity.githubUsername,
        developerName = portfolio.developerName,
        professionalSummary = portfolio.professionalSummary,
        topSkills = portfolio.topSkills,
        selectedProjects = portfolio.selectedProjects,
        skillsByCategory = portfolio.skillsByCategory,
        profileHighlights = portfolio.profileHighlights,
        totalPublicRepos = entity.totalPublicRepos,
        createdAt = entity.createdAt
    )

    private fun toSummaryResponse(entity: Portfolio, portfolio: DeveloperPortfolio) = PortfolioSummaryResponse(
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
