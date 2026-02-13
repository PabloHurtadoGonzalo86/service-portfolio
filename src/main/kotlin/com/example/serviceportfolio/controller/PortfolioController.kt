package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.GeneratePortfolioRequest
import com.example.serviceportfolio.dtos.PortfolioResponse
import com.example.serviceportfolio.dtos.PortfolioSummaryResponse
import com.example.serviceportfolio.services.PortfolioGenerationService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/portfolio")
class PortfolioController(
    private val portfolioGenerationService: PortfolioGenerationService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/generate")
    fun generatePortfolio(@Valid @RequestBody request: GeneratePortfolioRequest): ResponseEntity<PortfolioResponse> {
        logger.info("Portfolio generation requested for: {}", request.githubUsername)
        val response = portfolioGenerationService.generate(request.githubUsername)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getPortfolio(@PathVariable id: Long): ResponseEntity<PortfolioResponse> {
        val response = portfolioGenerationService.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun listPortfolios(): ResponseEntity<List<PortfolioSummaryResponse>> {
        val response = portfolioGenerationService.listAll()
        return ResponseEntity.ok(response)
    }
}
