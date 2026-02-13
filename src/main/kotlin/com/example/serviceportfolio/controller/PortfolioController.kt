package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.GeneratePortfolioRequest
import com.example.serviceportfolio.dtos.JobCreatedResponse
import com.example.serviceportfolio.dtos.JobStatusResponse
import com.example.serviceportfolio.dtos.PortfolioResponse
import com.example.serviceportfolio.dtos.PortfolioSummaryResponse
import com.example.serviceportfolio.entities.AsyncJobStatus
import com.example.serviceportfolio.services.AsyncPortfolioService
import com.example.serviceportfolio.services.PortfolioGenerationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/portfolio")
@Tag(name = "Portfolio Generation", description = "Generate professional developer portfolios from GitHub profiles")
class PortfolioController(
    private val portfolioGenerationService: PortfolioGenerationService,
    private val asyncPortfolioService: AsyncPortfolioService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Generate developer portfolio", description = "Scans a GitHub user's public repositories and generates a professional portfolio with AI")
    @ApiResponse(responseCode = "200", description = "Portfolio generated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid GitHub username")
    @ApiResponse(responseCode = "404", description = "GitHub user not found")
    @PostMapping("/generate")
    fun generatePortfolio(@Valid @RequestBody request: GeneratePortfolioRequest): ResponseEntity<PortfolioResponse> {
        logger.info("Portfolio generation requested for: {}", request.githubUsername)
        val response = portfolioGenerationService.generate(request.githubUsername)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Generate portfolio asynchronously", description = "Starts async portfolio generation and returns a job ID for status polling")
    @ApiResponse(responseCode = "202", description = "Job created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid GitHub username")
    @PostMapping("/generate/async")
    fun generatePortfolioAsync(@Valid @RequestBody request: GeneratePortfolioRequest): ResponseEntity<JobCreatedResponse> {
        logger.info("Async portfolio generation requested for: {}", request.githubUsername)
        
        val job = asyncPortfolioService.createJob(request.githubUsername)
        asyncPortfolioService.processPortfolioGeneration(job.id, request.githubUsername)
        
        val response = JobCreatedResponse(
            jobId = job.id,
            status = AsyncJobStatus.PENDING,
            message = "Portfolio generation started. Use /api/v1/portfolio/status/${job.id} to check progress."
        )
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response)
    }

    @Operation(summary = "Get job status", description = "Check the status of an async portfolio generation job")
    @ApiResponse(responseCode = "200", description = "Job status retrieved")
    @ApiResponse(responseCode = "404", description = "Job not found")
    @GetMapping("/status/{jobId}")
    fun getJobStatus(@PathVariable jobId: Long): ResponseEntity<JobStatusResponse> {
        val job = asyncPortfolioService.getJobStatus(jobId)
        
        val response = JobStatusResponse(
            jobId = job.id,
            status = job.status,
            githubUsername = job.githubUsername,
            resultId = job.resultId,
            errorMessage = job.errorMessage,
            createdAt = job.createdAt,
            updatedAt = job.updatedAt
        )
        
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "Get portfolio by ID")
    @ApiResponse(responseCode = "200", description = "Portfolio found")
    @ApiResponse(responseCode = "404", description = "Portfolio not found")
    @GetMapping("/{id}")
    fun getPortfolio(@PathVariable id: Long): ResponseEntity<PortfolioResponse> {
        val response = portfolioGenerationService.getById(id)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "List all portfolios", description = "Returns summary of all generated portfolios")
    @GetMapping
    fun listPortfolios(): ResponseEntity<List<PortfolioSummaryResponse>> {
        val response = portfolioGenerationService.listAll()
        return ResponseEntity.ok(response)
    }
}
