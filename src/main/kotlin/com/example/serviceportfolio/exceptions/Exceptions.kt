package com.example.serviceportfolio.exceptions


class RepoNotFoundException(message: String) : RuntimeException(message)

class InvalidRepoUrlException(message: String) : RuntimeException(message)

class InvalidRepoAnalysisException(message: String) : RuntimeException(message)

class GitHubApiException(message: String, cause: Throwable? = null) : RuntimeException(message , cause)

class AiAnalysisException(message: String, cause: Throwable? = null) : RuntimeException(message , cause)

class ReadmeCommitException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class RateLimitExceededException(message: String) : RuntimeException(message)

class AuthenticationRequiredException(message: String) : RuntimeException(message)

class UsageLimitExceededException(message: String) : RuntimeException(message)