package com.example.serviceportfolio.models

data class RepoSummary(
    val name: String,
    val description: String?,
    val primaryLanguage: String?,
    val stars: Int,
    val forks: Int,
    val url: String
)
