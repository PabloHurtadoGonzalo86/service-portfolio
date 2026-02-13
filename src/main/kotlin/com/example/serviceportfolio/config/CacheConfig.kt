package com.example.serviceportfolio.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    companion object {
        const val GITHUB_REPO_CONTEXT_CACHE = "githubRepoContext"
        const val GITHUB_USER_REPOS_CACHE = "githubUserRepos"
    }

    @Bean
    fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager(GITHUB_REPO_CONTEXT_CACHE, GITHUB_USER_REPOS_CACHE)
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(Duration.ofMinutes(30))
                .recordStats()
        )
        return cacheManager
    }
}
