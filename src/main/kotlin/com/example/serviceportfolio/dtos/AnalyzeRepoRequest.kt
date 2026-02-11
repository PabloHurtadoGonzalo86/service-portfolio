package com.example.serviceportfolio.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AnalyzeRepoRequest(
    @field:NotBlank(message = "Repo URL cannot be blank")
    @field:Pattern(regexp = "^https://github\\.com/[a-zA-Z0-9_-]+/[a-zA-Z0-9_-]+$", message = "Repo URL must be a valid GitHub URL")
    val repoUrl: String,
)
