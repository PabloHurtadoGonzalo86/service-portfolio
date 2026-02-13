package com.example.serviceportfolio.util

import com.example.serviceportfolio.exceptions.InvalidRepoUrlException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class GitHubUrlParserTest {

    @Test
    fun `parse valid HTTPS GitHub URL`() {
        val (owner, repo) = GitHubUrlParser.parse("https://github.com/owner/repo")
        assertEquals("owner", owner)
        assertEquals("repo", repo)
    }

    @Test
    fun `parse URL with git suffix`() {
        val (owner, repo) = GitHubUrlParser.parse("https://github.com/owner/repo.git")
        assertEquals("owner", owner)
        assertEquals("repo", repo)
    }

    @Test
    fun `parse URL with trailing slash`() {
        val (owner, repo) = GitHubUrlParser.parse("https://github.com/owner/repo/")
        assertEquals("owner", owner)
        assertEquals("repo", repo)
    }

    @Test
    fun `parse URL with www prefix`() {
        val (owner, repo) = GitHubUrlParser.parse("https://www.github.com/owner/repo")
        assertEquals("owner", owner)
        assertEquals("repo", repo)
    }

    @Test
    fun `parse URL with HTTP protocol`() {
        val (owner, repo) = GitHubUrlParser.parse("http://github.com/owner/repo")
        assertEquals("owner", owner)
        assertEquals("repo", repo)
    }

    @Test
    fun `parse URL without protocol`() {
        val (owner, repo) = GitHubUrlParser.parse("github.com/owner/repo")
        assertEquals("owner", owner)
        assertEquals("repo", repo)
    }

    @Test
    fun `parse URL with dots and hyphens in names`() {
        val (owner, repo) = GitHubUrlParser.parse("https://github.com/my-org/my.project-name")
        assertEquals("my-org", owner)
        assertEquals("my.project-name", repo)
    }

    @Test
    fun `parse URL with whitespace trimming`() {
        val (owner, repo) = GitHubUrlParser.parse("  https://github.com/owner/repo  ")
        assertEquals("owner", owner)
        assertEquals("repo", repo)
    }

    @Test
    fun `throws InvalidRepoUrlException for non-GitHub URL`() {
        assertThrows<InvalidRepoUrlException> {
            GitHubUrlParser.parse("https://gitlab.com/owner/repo")
        }
    }

    @Test
    fun `throws InvalidRepoUrlException for empty string`() {
        assertThrows<InvalidRepoUrlException> {
            GitHubUrlParser.parse("")
        }
    }

    @Test
    fun `throws InvalidRepoUrlException for URL with only owner`() {
        assertThrows<InvalidRepoUrlException> {
            GitHubUrlParser.parse("https://github.com/owner")
        }
    }

    @Test
    fun `throws InvalidRepoUrlException for random text`() {
        assertThrows<InvalidRepoUrlException> {
            GitHubUrlParser.parse("not a url")
        }
    }
}
