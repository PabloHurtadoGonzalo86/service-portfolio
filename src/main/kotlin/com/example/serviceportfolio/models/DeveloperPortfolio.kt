package com.example.serviceportfolio.models

data class DeveloperPortfolio(
    val developerName: String = "",
    val professionalSummary: String = "",
    val topSkills: List<String> = emptyList(),
    val selectedProjects: List<PortfolioProject> = emptyList(),
    val skillsByCategory: Map<String, List<String>> = emptyMap(),
    val profileHighlights: List<String> = emptyList()
)
