package com.example.serviceportfolio.models

data class RepoContext(
    val name: String,
    val description: String?,
    val language: String?,
    val languages: Map<String, Long>,
    val fileTree: List<String>,        // estructura de archivos
    val readmeContent: String?,        // README actual si existe
    val keyFiles: Map<String, String>,
)