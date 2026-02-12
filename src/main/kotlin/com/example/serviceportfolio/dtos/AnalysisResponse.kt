package com.example.serviceportfolio.dtos

import java.time.Instant

data class AnalysisResponse(
    val id: Long,
    val projectName: String,
    val description: String,
    val readmeContent: String,
    val techStack: List<String>,
    val detectedFeatures: List<String>,
    val createdAt: Instant
)
