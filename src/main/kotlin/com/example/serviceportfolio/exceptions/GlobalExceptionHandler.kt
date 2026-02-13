package com.example.serviceportfolio.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {


    @ExceptionHandler(RepoNotFoundException::class)
    fun handleRepoNotFoundException(ex: RepoNotFoundException): ProblemDetail  {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Repo not found")
        problem.title = "Repo not found"
        return problem
    }

    @ExceptionHandler(InvalidRepoUrlException::class)
    fun handleInvalidUrl(ex: InvalidRepoUrlException): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid URL")
        problem.title = "Invalid Repository URL"
        return problem
    }

    @ExceptionHandler(GitHubApiException::class)
    fun handleGitHubApi(ex: GitHubApiException): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.message ?: "GitHub API error")
        problem.title = "GitHub API Error"
        return problem
    }

    @ExceptionHandler(AiAnalysisException::class)
    fun handleAiAnalysis(ex: AiAnalysisException): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.message ?: "AI analysis error")
        problem.title = "AI Analysis Error"
        return problem
    }

    @ExceptionHandler(ReadmeCommitException::class)
    fun handleReadmeCommit(ex: ReadmeCommitException): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.message ?: "Failed to commit README")
        problem.title = "README Commit Error"
        return problem
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
        val errors = ex.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors.joinToString(", "))
        problem.title = "Validation Error"
        return problem
    }

    @ExceptionHandler(RateLimitExceededException::class)
    fun handleRateLimit(ex: RateLimitExceededException): ProblemDetail {
        val problem = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.message ?: "Rate limit exceeded")
        problem.title = "Rate Limit Exceeded"
        return problem
    }


}