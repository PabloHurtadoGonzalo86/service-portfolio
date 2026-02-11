package com.example.serviceportfolio.exceptions


class RepoNotFoundException(message: String) : RuntimeException(message)

class InvalidRepoUrlException(message: String) : RuntimeException(message)

class InvalidRepoAnalysisException(message: String) : RuntimeException(message)

class GitHubApiException(message: String, cause: Throwable? = null) : RuntimeException(message , cause)

class AiAnalysisException(message: String, cause: Throwable? = null) : RuntimeException(message , cause)