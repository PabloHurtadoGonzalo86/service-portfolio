package com.example.serviceportfolio.dtos

import java.time.Instant

data class DashboardResponse(
    val user: UserInfo,
    val stats: DashboardStats,
    val recentPortfolios: List<PortfolioSummaryResponse>,
    val recentAnalyses: List<AnalysisSummaryResponse>
)

data class UserInfo(
    val githubUsername: String,
    val name: String?,
    val email: String?,
    val avatarUrl: String?,
    val memberSince: Instant
)

data class DashboardStats(
    val totalPortfolios: Int,
    val totalAnalyses: Int
)

data class AnalysisSummaryResponse(
    val id: Long,
    val projectName: String,
    val repoUrl: String,
    val shortDescription: String,
    val techStack: List<String>,
    val createdAt: Instant
)
