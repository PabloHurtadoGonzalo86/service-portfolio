package com.example.serviceportfolio.dtos

import java.time.Instant
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val githubUsername: String,
    val name: String?,
    val email: String?,
    val avatarUrl: String?,
    val plan: String,
    val analysesUsed: Int,
    val portfoliosUsed: Int,
    val usageResetAt: Instant
)
