package com.example.serviceportfolio.services

import com.example.serviceportfolio.entities.AsyncJob
import com.example.serviceportfolio.entities.AsyncJobStatus
import com.example.serviceportfolio.repositories.AsyncJobRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AsyncPortfolioService(
    private val asyncJobRepository: AsyncJobRepository,
    private val portfolioGenerationService: PortfolioGenerationService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createJob(githubUsername: String): AsyncJob {
        val job = AsyncJob(
            jobType = "PORTFOLIO_GENERATION",
            githubUsername = githubUsername,
            status = AsyncJobStatus.PENDING
        )
        return asyncJobRepository.save(job)
    }

    @Async
    fun processPortfolioGeneration(jobId: Long, githubUsername: String) {
        logger.info("Starting async portfolio generation for job {} and user {}", jobId, githubUsername)
        
        val job = asyncJobRepository.findById(jobId).orElseThrow()
        job.status = AsyncJobStatus.PROCESSING
        asyncJobRepository.save(job)

        try {
            val response = portfolioGenerationService.generate(githubUsername)
            
            job.status = AsyncJobStatus.COMPLETED
            job.resultId = response.id
            asyncJobRepository.save(job)
            
            logger.info("Completed async portfolio generation for job {} with result ID {}", jobId, response.id)
        } catch (e: Exception) {
            logger.error("Error in async portfolio generation for job {}: {}", jobId, e.message, e)
            
            job.status = AsyncJobStatus.FAILED
            job.errorMessage = e.message ?: "Unknown error"
            asyncJobRepository.save(job)
        }
    }

    fun getJobStatus(jobId: Long): AsyncJob {
        return asyncJobRepository.findById(jobId).orElseThrow {
            IllegalArgumentException("Job not found with id: $jobId")
        }
    }

    fun listAllJobs(): List<AsyncJob> {
        return asyncJobRepository.findAllByOrderByCreatedAtDesc()
    }
}
