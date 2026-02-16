package com.example.serviceportfolio.controller

import com.example.serviceportfolio.config.AsyncConfig
import com.example.serviceportfolio.dtos.PortfolioResponse
import com.example.serviceportfolio.dtos.PortfolioSummaryResponse
import com.example.serviceportfolio.entities.User
import com.example.serviceportfolio.exceptions.RepoNotFoundException
import com.example.serviceportfolio.models.PortfolioProject
import com.example.serviceportfolio.security.CustomOAuth2User
import com.example.serviceportfolio.security.CustomOAuth2UserService
import com.example.serviceportfolio.security.OAuth2LoginSuccessHandler
import com.example.serviceportfolio.services.PortfolioGenerationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant
import java.util.UUID

@WebMvcTest(PortfolioController::class)
@Import(AsyncConfig::class)
class PortfolioControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var portfolioGenerationService: PortfolioGenerationService

    @MockitoBean
    private lateinit var customOAuth2UserService: CustomOAuth2UserService

    @MockitoBean
    private lateinit var oAuth2LoginSuccessHandler: OAuth2LoginSuccessHandler

    private fun createCustomOAuth2User(): CustomOAuth2User {
        val user = User(
            githubId = 12345L,
            githubUsername = "testuser"
        ).apply { id = UUID.fromString("00000000-0000-0000-0000-000000000001") }

        val attributes = mapOf<String, Any>("id" to 12345L, "login" to "testuser")
        val authority = OAuth2UserAuthority(attributes)
        val delegate = DefaultOAuth2User(listOf(authority), attributes, "login")
        return CustomOAuth2User(delegate, user)
    }

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
        `when`(portfolioGenerationService.generate(any(), anyOrNull())).thenReturn(response)

        val mvcResult = mockMvc.perform(
            post("/api/v1/portfolio/generate")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"githubUsername": "testuser"}""")
        )
            .andExpect(request().asyncStarted())
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
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
        `when`(portfolioGenerationService.generate(any(), anyOrNull()))
            .thenThrow(RepoNotFoundException("GitHub user not found: nonexistent"))

        val mvcResult = mockMvc.perform(
            post("/api/v1/portfolio/generate")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"githubUsername": "nonexistent"}""")
        )
            .andExpect(request().asyncStarted())
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
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

    // --- GET /api/v1/portfolio/my-portfolios ---

    @Test
    fun `list my portfolios returns 200 with user portfolios`() {
        val summary = PortfolioSummaryResponse(
            id = 1L,
            githubUsername = "testuser",
            developerName = "Test User",
            professionalSummary = "A skilled developer.",
            topSkills = listOf("Kotlin"),
            totalPublicRepos = 10,
            projectCount = 2,
            createdAt = Instant.now()
        )

        `when`(portfolioGenerationService.listByUser(any())).thenReturn(listOf(summary))

        val customUser = createCustomOAuth2User()

        mockMvc.perform(
            get("/api/v1/portfolio/my-portfolios")
                .with(oauth2Login().oauth2User(customUser))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].githubUsername").value("testuser"))
            .andExpect(jsonPath("$[0].projectCount").value(2))
    }

    @Test
    fun `list my portfolios without auth redirects to login`() {
        mockMvc.perform(get("/api/v1/portfolio/my-portfolios"))
            .andExpect(status().is3xxRedirection)
    }

    @Test
    fun `generate portfolio requires authentication`() {
        mockMvc.perform(
            post("/api/v1/portfolio/generate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"githubUsername": "testuser"}""")
        )
            .andExpect(status().is3xxRedirection)
    }
}
