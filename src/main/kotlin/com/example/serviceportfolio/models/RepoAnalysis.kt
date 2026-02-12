package com.example.serviceportfolio.models

data class RepoAnalysis(
    val projectName: String,
    val shortDescription: String,
    val techStack: List<String>,
    val detectedFeatures: List<String>,
    val readmeMarkdown: String
)
