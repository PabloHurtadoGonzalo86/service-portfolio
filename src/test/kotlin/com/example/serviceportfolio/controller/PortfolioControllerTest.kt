package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.PortfolioResponse
import com.example.serviceportfolio.dtos.PortfolioSummaryResponse
import com.example.serviceportfolio.exceptions.RepoNotFoundException
import com.example.serviceportfolio.models.PortfolioProject
import com.example.serviceportfolio.services.PortfolioGenerationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(PortfolioController::class)
class PortfolioControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var portfolioGenerationService: PortfolioGenerationService

    private fun samplePortfolioResponse() = PortfolioResponse(
        id = 1L,
        githubUsername = "testuser",
        developerName = "Test User",
        professionalSummary = "A skilled developer with experience in multiple technologies. They build robust backend systems and modern frontends.",
        topSkills = listOf("Kotlin", "Spring Boot", "React", "TypeScript"),
        selectedProjects = listOf(
            PortfolioProject(
                repoName = "my-api",
                repoUrl = "https://github.com/testuser/my-api",
                description = "A REST API built with Spring Boot and Kotlin. It provides endpoints for data processing and integrates with external services.\n\nThe project uses clean architecture patterns with proper separation of concerns between controllers, services, and repositories.",
                techStack = listOf("Kotlin", "Spring Boot", "PostgreSQL"),
                whyNotable = "Demonstrates strong backend architecture skills",
                category = "Backend"
            ),
            PortfolioProject(
                repoName = "my-frontend",
                repoUrl = "https://github.com/testuser/my-frontend",
                description = "A modern React application with TypeScript. Features responsive design and state management with Redux.\n\nIncludes comprehensive testing with Jest and React Testing Library, demonstrating commitment to code quality.",
                techStack = listOf("React", "TypeScript", "Redux"),
                whyNotable = "Shows full-stack capabilities with modern frontend tech",
                category = "Frontend"
            )
        ),
        skillsByCategory = mapOf(
            "Languages" to listOf("Kotlin", "TypeScript", "Java"),
            "Frameworks" to listOf("Spring Boot", "React")
        ),
        profileHighlights = listOf(
            "Full-stack developer with backend and frontend projects",
            "Experience with multiple programming languages"
        ),
        totalPublicRepos = 15,
        createdAt = Instant.now()
    )

    // --- POST /api/v1/portfolio/generate ---

    @Test
    fun `generate portfolio returns 200 with portfolio response`() {
        val response = samplePortfolioResponse()
        `when`(portfolioGenerationService.generate(any())).thenReturn(response)

        mockMvc.perform(
            post("/api/v1/portfolio/generate")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"githubUsername": "testuser"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.githubUsername").value("testuser"))
            .andExpect(jsonPath("$.developerName").value("Test User"))
            .andExpect(jsonPath("$.topSkills[0]").value("Kotlin"))
            .andExpect(jsonPath("$.selectedProjects[0].repoName").value("my-api"))
            .andExpect(jsonPath("$.selectedProjects[0].category").value("Backend"))
            .andExpect(jsonPath("$.selectedProjects[1].repoName").value("my-frontend"))
            .andExpect(jsonPath("$.skillsByCategory.Languages[0]").value("Kotlin"))
            .andExpect(jsonPath("$.profileHighlights").isArray)
            .andExpect(jsonPath("$.totalPublicRepos").value(15))
    }

    @Test
    fun `generate portfolio returns 400 for blank username`() {
        mockMvc.perform(
            post("/api/v1/portfolio/generate")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"githubUsername": ""}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `generate portfolio returns 404 when user not found`() {
        `when`(portfolioGenerationService.generate(any()))
            .thenThrow(RepoNotFoundException("GitHub user not found: nonexistent"))

        mockMvc.perform(
            post("/api/v1/portfolio/generate")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"githubUsername": "nonexistent"}""")
        )
            .andExpect(status().isNotFound)
    }

    // --- GET /api/v1/portfolio/{id} ---

    @Test
    fun `get portfolio by id returns 200`() {
        val response = samplePortfolioResponse()
        `when`(portfolioGenerationService.getById(1L)).thenReturn(response)

        mockMvc.perform(
            get("/api/v1/portfolio/1")
                .with(oauth2Login())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.githubUsername").value("testuser"))
            .andExpect(jsonPath("$.selectedProjects").isArray)
            .andExpect(jsonPath("$.selectedProjects.length()").value(2))
    }

    @Test
    fun `get portfolio by id returns 404 when not found`() {
        `when`(portfolioGenerationService.getById(999L))
            .thenThrow(RepoNotFoundException("Portfolio not found with id: 999"))

        mockMvc.perform(
            get("/api/v1/portfolio/999")
                .with(oauth2Login())
        )
            .andExpect(status().isNotFound)
    }

    // --- GET /api/v1/portfolio ---

    @Test
    fun `list portfolios returns 200 with summaries`() {
        val summary = PortfolioSummaryResponse(
            id = 1L,
            githubUsername = "testuser",
            developerName = "Test User",
            professionalSummary = "A skilled developer.",
            topSkills = listOf("Kotlin", "React"),
            totalPublicRepos = 15,
            projectCount = 2,
            createdAt = Instant.now()
        )

        `when`(portfolioGenerationService.listAll()).thenReturn(listOf(summary))

        mockMvc.perform(
            get("/api/v1/portfolio")
                .with(oauth2Login())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].githubUsername").value("testuser"))
            .andExpect(jsonPath("$[0].projectCount").value(2))
            .andExpect(jsonPath("$[0].topSkills[0]").value("Kotlin"))
    }

    @Test
    fun `list portfolios returns 200 with empty list`() {
        `when`(portfolioGenerationService.listAll()).thenReturn(emptyList())

        mockMvc.perform(
            get("/api/v1/portfolio")
                .with(oauth2Login())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)
    }
}
