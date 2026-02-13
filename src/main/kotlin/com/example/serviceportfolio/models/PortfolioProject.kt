package com.example.serviceportfolio.models

data class PortfolioProject(
    val repoName: String = "",
    val repoUrl: String = "",
    val description: String = "",
    val techStack: List<String> = emptyList(),
    val whyNotable: String = "",
    val category: String = ""
)
