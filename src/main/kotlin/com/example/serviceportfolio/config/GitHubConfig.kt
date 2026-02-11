package com.example.serviceportfolio.config

import com.example.serviceportfolio.client.GitHubAppTokenService
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitHubConfig {

    @Bean
    fun github(tokenService: GitHubAppTokenService) = GitHub {
        return GitHubBuilder().withAppInstallationToken(tokenService.getInstallationToken()).build()
    }
}