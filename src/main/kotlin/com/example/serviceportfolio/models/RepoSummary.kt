package com.example.serviceportfolio.models

data class RepoSummary(
    val name: String,
    val description: String?,
    val primaryLanguage: String?,
    val languages: Map<String, Long>,
    val stars: Int,
    val forks: Int,
    val sizeKb: Int,
    val url: String
)
