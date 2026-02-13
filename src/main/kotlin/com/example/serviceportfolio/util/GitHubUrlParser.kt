package com.example.serviceportfolio.util

import com.example.serviceportfolio.exceptions.InvalidRepoUrlException

object GitHubUrlParser {

    private val GITHUB_URL_REGEX = Regex(
        """^(?:https?://)?(?:www\.)?github\.com/([^/]+)/([^/]+?)(?:\.git)?/?$"""
    )

    fun parse(url: String): Pair<String, String> {
        val matchResult = GITHUB_URL_REGEX.matchEntire(url.trim())
            ?: throw InvalidRepoUrlException("Invalid GitHub repository URL: $url")

        val owner = matchResult.groupValues[1]
        val repo = matchResult.groupValues[2]
        return Pair(owner, repo)
    }
}
