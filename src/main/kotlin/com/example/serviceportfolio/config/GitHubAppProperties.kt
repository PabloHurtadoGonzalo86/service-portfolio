package com.example.serviceportfolio.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@Component
@ConfigurationProperties(prefix = "github.app")
data class GitHubAppProperties(
    val appId: Long,
    val installationId: Long,
    val privateKeyPath: String
)
