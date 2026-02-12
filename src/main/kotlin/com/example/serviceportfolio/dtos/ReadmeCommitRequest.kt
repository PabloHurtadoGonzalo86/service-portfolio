package com.example.serviceportfolio.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ReadmeCommitRequest(
    @field:NotBlank(message = "Repo URL cannot be blank")
    @field:Pattern(regexp = "^https://github\\.com/[a-zA-Z0-9_.-]+/[a-zA-Z0-9._-]+(?:\\.git)?$", message = "Repo URL must be a valid GitHub URL")
    val repoUrl: String,

    @field:NotBlank(message = "README content cannot be blank")
    @field:Size(max = 500_000, message = "README content must not exceed 500,000 characters")
    val readmeContent: String
)
