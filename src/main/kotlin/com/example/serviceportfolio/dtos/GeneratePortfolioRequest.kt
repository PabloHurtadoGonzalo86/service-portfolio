package com.example.serviceportfolio.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class GeneratePortfolioRequest(
    @field:NotBlank(message = "GitHub username cannot be blank")
    @field:Size(max = 39, message = "GitHub username must not exceed 39 characters")
    val githubUsername: String
)
