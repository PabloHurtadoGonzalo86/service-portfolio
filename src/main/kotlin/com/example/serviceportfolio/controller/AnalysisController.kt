package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.AnalysisResponse
import com.example.serviceportfolio.dtos.AnalyzeRepoRequest
import com.example.serviceportfolio.dtos.ReadmeCommitRequest
import com.example.serviceportfolio.dtos.ReadmeCommitResponse
import com.example.serviceportfolio.entities.AnalysisResult
import com.example.serviceportfolio.exceptions.RepoNotFoundException
import com.example.serviceportfolio.repositories.AnalysisResultRepository
import com.example.serviceportfolio.services.AiAnalysisService
import com.example.serviceportfolio.services.GitHubRepoService
import com.example.serviceportfolio.services.ReadmeCommitService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult

@RestController
@RequestMapping("/api/v1/repos")
@Tag(name = "Repository Analysis", description = "Analyze GitHub repositories and generate READMEs")
class AnalysisController(
    private val gitHubRepoService: GitHubRepoService,
    private val aiAnalysisService: AiAnalysisService,
    private val analysisResultRepository: AnalysisResultRepository,
    private val readmeCommitService: ReadmeCommitService,
    @Qualifier("aiTaskExecutor") private val taskExecutor: ThreadPoolTaskExecutor
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Analyze a GitHub repository", description = "Reads a GitHub repo, analyzes the code with AI, and generates a professional README")
    @ApiResponse(responseCode = "200", description = "Analysis completed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid repository URL")
    @ApiResponse(responseCode = "404", description = "Repository not found")
    @PostMapping("/analyze")
    fun analyzeRepo(@Valid @RequestBody request: AnalyzeRepoRequest): DeferredResult<ResponseEntity<AnalysisResponse>> {
        logger.info("Solicitud de análisis recibida para: {}", request.repoUrl)

        val deferredResult = DeferredResult<ResponseEntity<AnalysisResponse>>(120_000L)
        deferredResult.onTimeout {
            logger.warn("Timeout al analizar repositorio: {}", request.repoUrl)
            deferredResult.setErrorResult(
                RuntimeException("Analysis timed out for ${request.repoUrl}")
            )
        }

        taskExecutor.execute {
            try {
                val existing = analysisResultRepository.findFirstByRepoUrlOrderByCreatedAtDesc(request.repoUrl)
                if (existing.isPresent) {
                    logger.info("Análisis existente encontrado para: {}, id: {}", request.repoUrl, existing.get().id)
                    deferredResult.setResult(ResponseEntity.ok(existing.get().toResponse()))
                    return@execute
                }

                val repoContext = gitHubRepoService.getRepoContext(request.repoUrl)
                val analysis = aiAnalysisService.analyze(repoContext)

                val entity = AnalysisResult(
                    repoUrl = request.repoUrl,
                    projectName = analysis.projectName,
                    shortDescription = analysis.shortDescription,
                    techStack = analysis.techStack,
                    detectedFeatures = analysis.detectedFeatures,
                    readmeContent = analysis.readmeMarkdown
                )
                val saved = analysisResultRepository.save(entity)

                logger.info("Análisis guardado con id: {}", saved.id)
                deferredResult.setResult(ResponseEntity.ok(saved.toResponse()))
            } catch (e: Exception) {
                logger.error("Error al analizar repositorio {}: {}", request.repoUrl, e.message)
                deferredResult.setErrorResult(e)
            }
        }

        return deferredResult
    }

    @Operation(summary = "List all analyses", description = "Returns all repository analyses ordered by creation date")
    @GetMapping("/analyses")
    fun listAnalyses(): ResponseEntity<List<AnalysisResponse>> {
        val results = analysisResultRepository.findAllByOrderByCreatedAtDesc()
        return ResponseEntity.ok(results.map { it.toResponse() })
    }

    @Operation(summary = "Get analysis by ID", description = "Returns a specific repository analysis")
    @ApiResponse(responseCode = "200", description = "Analysis found")
    @ApiResponse(responseCode = "404", description = "Analysis not found")
    @GetMapping("/analyses/{id}")
    fun getAnalysis(@PathVariable id: Long): ResponseEntity<AnalysisResponse> {
        val result = analysisResultRepository.findById(id)
            .orElseThrow { RepoNotFoundException("Analysis not found with id: $id") }
        return ResponseEntity.ok(result.toResponse())
    }

    @Operation(summary = "Commit README to repository", description = "Commits a generated README directly to the user's GitHub repository. Requires OAuth authentication.")
    @ApiResponse(responseCode = "200", description = "README committed successfully")
    @ApiResponse(responseCode = "401", description = "OAuth authentication required")
    @ApiResponse(responseCode = "502", description = "GitHub API error during commit")
    @PostMapping("/readme/commit")
    fun commitReadme(
        @Valid @RequestBody request: ReadmeCommitRequest,
        @RegisteredOAuth2AuthorizedClient("github") authorizedClient: OAuth2AuthorizedClient
    ): ResponseEntity<ReadmeCommitResponse> {
        logger.info("Solicitud de commit de README recibida para: {}", request.repoUrl)

        val token = authorizedClient.accessToken.tokenValue
        val response = readmeCommitService.commitReadme(request.repoUrl, request.readmeContent, token)

        logger.info("README committed exitosamente, sha: {}", response.commitSha)
        return ResponseEntity.ok(response)
    }

    private fun AnalysisResult.toResponse() = AnalysisResponse(
        id = id,
        projectName = projectName,
        description = shortDescription,
        readmeContent = readmeContent,
        techStack = techStack,
        detectedFeatures = detectedFeatures,
        createdAt = createdAt
    )
}
