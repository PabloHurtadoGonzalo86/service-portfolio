package com.example.serviceportfolio.services

import com.example.serviceportfolio.dtos.ReadmeCommitResponse
import com.example.serviceportfolio.exceptions.GitHubApiException
import com.example.serviceportfolio.exceptions.ReadmeCommitException
import com.example.serviceportfolio.exceptions.RepoNotFoundException
import com.example.serviceportfolio.util.GitHubUrlParser
import org.kohsuke.github.GHFileNotFoundException
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.HttpException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class ReadmeCommitService {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun commitReadme(repoUrl: String, readmeContent: String, oauthToken: String): ReadmeCommitResponse {
        val (owner, repo) = GitHubUrlParser.parse(repoUrl)
        logger.info("Committing README to {}/{}", owner, repo)

        try {
            val gitHub = GitHubBuilder()
                .withOAuthToken(oauthToken)
                .build()

            val repository = gitHub.getRepository("$owner/$repo")

            // Intentar obtener el README existente para conseguir su SHA (necesario para update)
            val existingSha = try {
                repository.getFileContent("README.md").sha
            } catch (e: GHFileNotFoundException) {
                null
            }

            val response = if (existingSha != null) {
                logger.info("README.md exists in {}/{}, updating (sha: {})", owner, repo, existingSha)
                repository.createContent()
                    .content(readmeContent)
                    .path("README.md")
                    .sha(existingSha)
                    .message("docs: update README.md via Service Portfolio")
                    .commit()
            } else {
                logger.info("README.md does not exist in {}/{}, creating", owner, repo)
                repository.createContent()
                    .content(readmeContent)
                    .path("README.md")
                    .message("docs: add README.md via Service Portfolio")
                    .commit()
            }

            val commitSha = response.commit.sha
            val commitUrl = response.commit.htmlUrl

            logger.info("README committed successfully to {}/{}, sha: {}", owner, repo, commitSha)

            return ReadmeCommitResponse(
                commitSha = commitSha,
                commitUrl = commitUrl
            )

        } catch (e: GHFileNotFoundException) {
            throw RepoNotFoundException("Repository not found or not accessible: $owner/$repo")
        } catch (e: HttpException) {
            when (e.responseCode) {
                403 -> throw ReadmeCommitException(
                    "Insufficient permissions to commit to $owner/$repo. The OAuth token may lack the 'repo' scope.",
                    cause = e
                )
                409 -> throw ReadmeCommitException(
                    "Conflict while committing README to $owner/$repo. The file may have been modified concurrently.",
                    cause = e
                )
                else -> throw ReadmeCommitException(
                    "GitHub API error while committing README to $owner/$repo: ${e.message}",
                    cause = e
                )
            }
        } catch (e: IOException) {
            throw GitHubApiException("Network error while committing README to GitHub: ${e.message}", cause = e)
        }
    }
}
