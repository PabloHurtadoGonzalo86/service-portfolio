package com.example.serviceportfolio.config

import com.example.serviceportfolio.client.GitHubAppTokenService
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(GitHubAppProperties::class)
class GitHubConfig {

    @Bean
    fun gitHub(tokenService: GitHubAppTokenService): GitHub {
        return GitHubBuilder()
            .withAppInstallationToken(tokenService.getInstallationToken())
            .build()
    }
}