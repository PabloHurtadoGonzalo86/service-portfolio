package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.JobCreatedResponse
import com.example.serviceportfolio.dtos.JobStatusResponse
import com.example.serviceportfolio.entities.AsyncJob
import com.example.serviceportfolio.entities.AsyncJobStatus
import com.example.serviceportfolio.exceptions.JobNotFoundException
import com.example.serviceportfolio.services.AsyncPortfolioService
import com.example.serviceportfolio.services.PortfolioGenerationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

@WebMvcTest(PortfolioController::class)
class AsyncPortfolioControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var portfolioGenerationService: PortfolioGenerationService

    @MockitoBean
    private lateinit var asyncPortfolioService: AsyncPortfolioService

    @Test
    fun `generate portfolio async returns 202 with job info`() {
        val job = AsyncJob(
            jobType = "PORTFOLIO_GENERATION",
            githubUsername = "testuser",
            status = AsyncJobStatus.PENDING
        )
        job.id = 123L

        `when`(asyncPortfolioService.createJob(any())).thenReturn(job)

        mockMvc.perform(
            post("/api/v1/portfolio/generate/async")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"githubUsername": "testuser"}""")
        )
            .andExpect(status().isAccepted)
            .andExpect(jsonPath("$.jobId").value(123))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.message").exists())

        verify(asyncPortfolioService).createJob("testuser")
        verify(asyncPortfolioService).processPortfolioGeneration(123L, "testuser")
    }

    @Test
    fun `get job status returns 200 with job details`() {
        val job = AsyncJob(
            jobType = "PORTFOLIO_GENERATION",
            githubUsername = "testuser",
            status = AsyncJobStatus.COMPLETED
        )
        job.id = 123L
        job.resultId = 456L

        `when`(asyncPortfolioService.getJobStatus(123L)).thenReturn(job)

        mockMvc.perform(
            get("/api/v1/portfolio/status/123")
                .with(oauth2Login())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.jobId").value(123))
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.githubUsername").value("testuser"))
            .andExpect(jsonPath("$.resultId").value(456))
    }

    @Test
    fun `get job status returns 404 when job not found`() {
        `when`(asyncPortfolioService.getJobStatus(999L))
            .thenThrow(JobNotFoundException("Job not found with id: 999"))

        mockMvc.perform(
            get("/api/v1/portfolio/status/999")
                .with(oauth2Login())
        )
            .andExpect(status().isNotFound)
    }
}
