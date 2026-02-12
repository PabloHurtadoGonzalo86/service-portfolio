package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.AnalysisResponse
import com.example.serviceportfolio.dtos.AnalyzeRepoRequest
import com.example.serviceportfolio.entities.AnalysisResult
import com.example.serviceportfolio.exceptions.RepoNotFoundException
import com.example.serviceportfolio.repositories.AnalysisResultRepository
import com.example.serviceportfolio.services.AiAnalysisService
import com.example.serviceportfolio.services.GitHubRepoService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/repos")
class AnalysisController(
    private val gitHubRepoService: GitHubRepoService,
    private val aiAnalysisService: AiAnalysisService,
    private val analysisResultRepository: AnalysisResultRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/analyze")
    fun analyzeRepo(@Valid @RequestBody request: AnalyzeRepoRequest): ResponseEntity<AnalysisResponse> {
        logger.info("Solicitud de análisis recibida para: {}", request.repoUrl)

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
        return ResponseEntity.ok(saved.toResponse())
    }

    @GetMapping("/analyses")
    fun listAnalyses(): ResponseEntity<List<AnalysisResponse>> {
        val results = analysisResultRepository.findAllByOrderByCreatedAtDesc()
        return ResponseEntity.ok(results.map { it.toResponse() })
    }

    @GetMapping("/analyses/{id}")
    fun getAnalysis(@PathVariable id: Long): ResponseEntity<AnalysisResponse> {
        val result = analysisResultRepository.findById(id)
            .orElseThrow { RepoNotFoundException("Analysis not found with id: $id") }
        return ResponseEntity.ok(result.toResponse())
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
