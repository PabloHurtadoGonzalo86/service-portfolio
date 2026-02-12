package com.example.serviceportfolio.services

import com.example.serviceportfolio.models.RepoContext
import com.example.serviceportfolio.exceptions.GitHubApiException
import com.example.serviceportfolio.exceptions.InvalidRepoUrlException
import com.example.serviceportfolio.exceptions.RepoNotFoundException
import org.kohsuke.github.GHFileNotFoundException
import org.kohsuke.github.GitHub
import org.kohsuke.github.HttpException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class GitHubRepoService(
    private val gitHub: GitHub
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private val GITHUB_URL_REGEX = Regex(
            """^(?:https?://)?(?:www\.)?github\.com/([^/]+)/([^/]+?)(?:\.git)?/?$"""
        )
        private const val MAX_FILE_TREE_SIZE = 1000
        private val KEY_FILES = listOf(
            "package.json",
            "build.gradle.kts",
            "build.gradle",
            "pom.xml",
            "Cargo.toml",
            "go.mod",
            "requirements.txt",
            "pyproject.toml",
            "Gemfile",
            "composer.json",
            "Dockerfile",
            "docker-compose.yml"
        )
    }

    fun getRepoContext(repoUrl: String): RepoContext {
        val (owner, repo) = parseGitHubUrl(repoUrl)
        logger.info("Fetching repo context for {}/{}", owner, repo)

        try {
            val repository = gitHub.getRepository("$owner/$repo")

            val name = repository.name
            val description = repository.description
            val language = repository.language
            val languages = repository.listLanguages()
            val fileTree = getFileTree(repository)
            val readmeContent = getReadmeContent(repository)
            val keyFiles = getKeyFiles(repository)

            return RepoContext(
                name = name,
                description = description,
                language = language,
                languages = languages,
                fileTree = fileTree,
                readmeContent = readmeContent,
                keyFiles = keyFiles
            )

        } catch (e: GHFileNotFoundException) {
            throw RepoNotFoundException("Repository not found or not accessible: $owner/$repo")
        } catch (e: HttpException) {
            throw GitHubApiException("GitHub API error while accessing $owner/$repo: ${e.message}", cause = e)
        } catch (e: IOException) {
            throw GitHubApiException("Network error while accessing GitHub: ${e.message}", cause = e)
        }
    }

    private fun parseGitHubUrl(url: String): Pair<String, String> {
        val matchResult = GITHUB_URL_REGEX.matchEntire(url.trim())
            ?: throw InvalidRepoUrlException("Invalid GitHub repository URL: $url")

        val owner = matchResult.groupValues[1]
        val repo = matchResult.groupValues[2]
        return Pair(owner, repo)
    }

    private fun getFileTree(repository: org.kohsuke.github.GHRepository): List<String> {
        return try {
            val defaultBranch = repository.defaultBranch
            val tree = repository.getTreeRecursive(defaultBranch, 1)

            tree.tree
                .filter { it.type == "blob" }
                .map { it.path }
                .take(MAX_FILE_TREE_SIZE)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getReadmeContent(repository: org.kohsuke.github.GHRepository): String? {
        return try {
            val readme = repository.getReadme()
            readme.read().bufferedReader().use { it.readText() }
        } catch (e: GHFileNotFoundException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun getKeyFiles(repository: org.kohsuke.github.GHRepository): Map<String, String> {
        val result = mutableMapOf<String, String>()

        for (fileName in KEY_FILES) {
            try {
                val fileContent = repository.getFileContent(fileName)
                val content = fileContent.read().bufferedReader().use { it.readText() }
                result[fileName] = content
            } catch (e: GHFileNotFoundException) {
                continue
            } catch (e: Exception) {
                continue
            }
        }

        return result
    }
}
