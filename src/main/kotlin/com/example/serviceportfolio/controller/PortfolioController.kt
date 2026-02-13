package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.GeneratePortfolioRequest
import com.example.serviceportfolio.dtos.PortfolioResponse
import com.example.serviceportfolio.dtos.PortfolioSummaryResponse
import com.example.serviceportfolio.services.PortfolioGenerationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult

@RestController
@RequestMapping("/api/v1/portfolio")
@Tag(name = "Portfolio Generation", description = "Generate professional developer portfolios from GitHub profiles")
class PortfolioController(
    private val portfolioGenerationService: PortfolioGenerationService,
    @Qualifier("aiTaskExecutor") private val taskExecutor: ThreadPoolTaskExecutor
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Generate developer portfolio", description = "Scans a GitHub user's public repositories and generates a professional portfolio with AI")
    @ApiResponse(responseCode = "200", description = "Portfolio generated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid GitHub username")
    @ApiResponse(responseCode = "404", description = "GitHub user not found")
    @PostMapping("/generate")
    fun generatePortfolio(@Valid @RequestBody request: GeneratePortfolioRequest): DeferredResult<ResponseEntity<PortfolioResponse>> {
        logger.info("Portfolio generation requested for: {}", request.githubUsername)

        val deferredResult = DeferredResult<ResponseEntity<PortfolioResponse>>(120_000L)
        deferredResult.onTimeout {
            logger.warn("Timeout generating portfolio for: {}", request.githubUsername)
            deferredResult.setErrorResult(
                RuntimeException("Portfolio generation timed out for ${request.githubUsername}")
            )
        }

        taskExecutor.execute {
            try {
                val response = portfolioGenerationService.generate(request.githubUsername)
                deferredResult.setResult(ResponseEntity.ok(response))
            } catch (e: Exception) {
                logger.error("Error generating portfolio for {}: {}", request.githubUsername, e.message)
                deferredResult.setErrorResult(e)
            }
        }

        return deferredResult
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
