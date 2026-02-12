package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.AnalysisResponse
import com.example.serviceportfolio.dtos.AnalyzeRepoRequest
import com.example.serviceportfolio.services.AiAnalysisService
import com.example.serviceportfolio.services.GitHubRepoService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/repos")
class AnalysisController(
    private val gitHubRepoService: GitHubRepoService,
    private val aiAnalysisService: AiAnalysisService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/analyze")
    fun analyzeRepo(@Valid @RequestBody request: AnalyzeRepoRequest): ResponseEntity<AnalysisResponse> {
        logger.info("Solicitud de análisis recibida para: {}", request.repoUrl)

        val repoContext = gitHubRepoService.getRepoContext(request.repoUrl)
        val analysis = aiAnalysisService.analyze(repoContext)

        val response = AnalysisResponse(
            projectName = analysis.projectName,
            description = analysis.shortDescription,
            readmeContent = analysis.readmeMarkdown
        )

        return ResponseEntity.ok(response)
    }
}
