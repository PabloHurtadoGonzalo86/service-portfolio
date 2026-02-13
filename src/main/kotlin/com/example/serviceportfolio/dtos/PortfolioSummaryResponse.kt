package com.example.serviceportfolio.dtos

import java.time.Instant

data class PortfolioSummaryResponse(
    val id: Long,
    val githubUsername: String,
    val developerName: String,
    val professionalSummary: String,
    val topSkills: List<String>,
    val totalPublicRepos: Int,
    val projectCount: Int,
    val createdAt: Instant
)
