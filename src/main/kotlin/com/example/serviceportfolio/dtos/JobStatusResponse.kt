package com.example.serviceportfolio.dtos

import com.example.serviceportfolio.entities.AsyncJobStatus
import java.time.Instant

data class JobStatusResponse(
    val jobId: Long,
    val status: AsyncJobStatus,
    val githubUsername: String,
    val resultId: Long? = null,
    val errorMessage: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class JobCreatedResponse(
    val jobId: Long,
    val status: AsyncJobStatus,
    val message: String
)
