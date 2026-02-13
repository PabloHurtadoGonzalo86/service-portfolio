package com.example.serviceportfolio.dtos

import com.example.serviceportfolio.models.PortfolioProject
import java.time.Instant

data class PortfolioResponse(
    val id: Long,
    val githubUsername: String,
    val developerName: String,
    val professionalSummary: String,
    val topSkills: List<String>,
    val selectedProjects: List<PortfolioProject>,
    val skillsByCategory: Map<String, List<String>>,
    val profileHighlights: List<String>,
    val totalPublicRepos: Int,
    val createdAt: Instant
)
