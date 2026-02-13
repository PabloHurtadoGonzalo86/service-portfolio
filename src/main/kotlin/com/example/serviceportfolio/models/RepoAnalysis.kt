package com.example.serviceportfolio.models

data class RepoAnalysis(
    val projectName: String = "",
    val shortDescription: String = "",
    val techStack: List<String> = emptyList(),
    val detectedFeatures: List<String> = emptyList(),
    val readmeMarkdown: String = ""
)
