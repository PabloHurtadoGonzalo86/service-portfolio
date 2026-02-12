package com.example.serviceportfolio.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(GitHubAppProperties::class)
class GitHubConfig