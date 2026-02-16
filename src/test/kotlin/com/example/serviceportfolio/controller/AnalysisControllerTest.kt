package com.example.serviceportfolio.controller

import com.example.serviceportfolio.config.AsyncConfig
import com.example.serviceportfolio.dtos.ReadmeCommitResponse
import com.example.serviceportfolio.entities.AnalysisResult
import com.example.serviceportfolio.entities.User
import com.example.serviceportfolio.models.RepoAnalysis
import com.example.serviceportfolio.models.RepoContext
import com.example.serviceportfolio.repositories.AnalysisResultRepository
import com.example.serviceportfolio.security.CustomOAuth2User
import com.example.serviceportfolio.security.CustomOAuth2UserService
import com.example.serviceportfolio.security.OAuth2LoginSuccessHandler
import com.example.serviceportfolio.services.AiAnalysisService
import com.example.serviceportfolio.services.GitHubRepoService
import com.example.serviceportfolio.services.ReadmeCommitService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Client
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Optional
import java.util.UUID

@WebMvcTest(AnalysisController::class)
@Import(AsyncConfig::class)
class AnalysisControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var gitHubRepoService: GitHubRepoService

    @MockitoBean
    private lateinit var aiAnalysisService: AiAnalysisService

    @MockitoBean
    private lateinit var analysisResultRepository: AnalysisResultRepository

    @MockitoBean
    private lateinit var readmeCommitService: ReadmeCommitService

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

    // --- POST /api/v1/repos/analyze ---

    @Test
    fun `analyze repo returns 200 with analysis response`() {
        val repoContext = RepoContext(
            name = "test-repo",
            description = "A test repo",
            language = "Kotlin",
            languages = mapOf("Kotlin" to 1000L),
            fileTree = listOf("src/main/App.kt"),
            readmeContent = null,
            keyFiles = mapOf("build.gradle.kts" to "plugins { }")
        )

        val repoAnalysis = RepoAnalysis(
            projectName = "test-repo",
            shortDescription = "A test project",
            techStack = listOf("Kotlin", "Spring Boot"),
            detectedFeatures = listOf("REST API"),
            readmeMarkdown = "# Test Repo\nA test project."
        )

        val savedEntity = AnalysisResult(
            repoUrl = "https://github.com/owner/test-repo",
            projectName = "test-repo",
            shortDescription = "A test project",
            techStack = listOf("Kotlin", "Spring Boot"),
            detectedFeatures = listOf("REST API"),
            readmeContent = "# Test Repo\nA test project."
        ).apply { id = 1L }

        `when`(analysisResultRepository.findFirstByRepoUrlOrderByCreatedAtDesc(any()))
            .thenReturn(Optional.empty())
        `when`(gitHubRepoService.getRepoContext(any())).thenReturn(repoContext)
        `when`(aiAnalysisService.analyze(any())).thenReturn(repoAnalysis)
        `when`(analysisResultRepository.save(any())).thenReturn(savedEntity)

        val mvcResult = mockMvc.perform(
            post("/api/v1/repos/analyze")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"repoUrl": "https://github.com/owner/test-repo"}""")
        )
            .andExpect(request().asyncStarted())
            .andReturn()

        mockMvc.perform(asyncDispatch(mvcResult))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.projectName").value("test-repo"))
            .andExpect(jsonPath("$.description").value("A test project"))
            .andExpect(jsonPath("$.readmeContent").value("# Test Repo\nA test project."))
            .andExpect(jsonPath("$.techStack[0]").value("Kotlin"))
            .andExpect(jsonPath("$.detectedFeatures[0]").value("REST API"))
    }

    @Test
    fun `analyze repo returns 400 for invalid URL`() {
        mockMvc.perform(
            post("/api/v1/repos/analyze")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"repoUrl": "not-a-valid-url"}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `analyze repo returns 400 for blank URL`() {
        mockMvc.perform(
            post("/api/v1/repos/analyze")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"repoUrl": ""}""")
        )
            .andExpect(status().isBadRequest)
    }

    // --- GET /api/v1/repos/analyses ---

    @Test
    fun `list analyses returns 200 with list`() {
        val entity = AnalysisResult(
            repoUrl = "https://github.com/owner/repo",
            projectName = "repo",
            shortDescription = "desc",
            techStack = listOf("Java"),
            detectedFeatures = listOf("API"),
            readmeContent = "# Readme"
        ).apply { id = 1L }

        `when`(analysisResultRepository.findAllByOrderByCreatedAtDesc()).thenReturn(listOf(entity))

        mockMvc.perform(
            get("/api/v1/repos/analyses")
                .with(oauth2Login())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].projectName").value("repo"))
            .andExpect(jsonPath("$[0].description").value("desc"))
    }

    // --- GET /api/v1/repos/analyses/{id} ---

    @Test
    fun `get analysis by id returns 200`() {
        val entity = AnalysisResult(
            repoUrl = "https://github.com/owner/repo",
            projectName = "repo",
            shortDescription = "desc",
            techStack = listOf("Java"),
            detectedFeatures = listOf("API"),
            readmeContent = "# Readme"
        ).apply { id = 1L }

        `when`(analysisResultRepository.findById(1L)).thenReturn(Optional.of(entity))

        mockMvc.perform(
            get("/api/v1/repos/analyses/1")
                .with(oauth2Login())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.projectName").value("repo"))
    }

    @Test
    fun `get analysis by id returns 404 when not found`() {
        `when`(analysisResultRepository.findById(999L)).thenReturn(Optional.empty())

        mockMvc.perform(
            get("/api/v1/repos/analyses/999")
                .with(oauth2Login())
        )
            .andExpect(status().isNotFound)
    }

    // --- POST /api/v1/repos/readme/commit ---

    @Test
    fun `commit readme returns 200 with commit response`() {
        val commitResponse = ReadmeCommitResponse(
            commitSha = "abc123def456",
            commitUrl = "https://github.com/owner/repo/commit/abc123def456"
        )

        `when`(readmeCommitService.commitReadme(any(), any(), any())).thenReturn(commitResponse)

        mockMvc.perform(
            post("/api/v1/repos/readme/commit")
                .with(oauth2Login())
                .with(oauth2Client("github"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"repoUrl": "https://github.com/owner/repo", "readmeContent": "# My README"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.commitSha").value("abc123def456"))
            .andExpect(jsonPath("$.commitUrl").value("https://github.com/owner/repo/commit/abc123def456"))
    }

    @Test
    fun `commit readme returns 400 for blank readme content`() {
        mockMvc.perform(
            post("/api/v1/repos/readme/commit")
                .with(oauth2Login())
                .with(oauth2Client("github"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"repoUrl": "https://github.com/owner/repo", "readmeContent": ""}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `commit readme requires authentication`() {
        mockMvc.perform(
            post("/api/v1/repos/readme/commit")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"repoUrl": "https://github.com/owner/repo", "readmeContent": "# README"}""")
        )
            .andExpect(status().is3xxRedirection) // Redirige al login de OAuth2
    }

    // --- GET /api/v1/repos/my-analyses ---

    @Test
    fun `list my analyses returns 200 with user analyses`() {
        val entity = AnalysisResult(
            repoUrl = "https://github.com/owner/repo",
            projectName = "repo",
            shortDescription = "desc",
            techStack = listOf("Kotlin"),
            detectedFeatures = listOf("API"),
            readmeContent = "# Readme"
        ).apply { id = 1L }

        `when`(analysisResultRepository.findAllByUserOrderByCreatedAtDesc(any()))
            .thenReturn(listOf(entity))

        val customUser = createCustomOAuth2User()

        mockMvc.perform(
            get("/api/v1/repos/my-analyses")
                .with(oauth2Login().oauth2User(customUser))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].projectName").value("repo"))
    }

    @Test
    fun `list my analyses without auth redirects to login`() {
        mockMvc.perform(get("/api/v1/repos/my-analyses"))
            .andExpect(status().is3xxRedirection)
    }
}
