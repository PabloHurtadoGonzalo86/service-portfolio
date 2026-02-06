package com.example.serviceportfolio.dtos

data class RepoContext(
    val name: String,
    val description: String,
    val language: String,
    val languages: Map<String, Long>,
    val keyFiles: Map<String, String>,
)
