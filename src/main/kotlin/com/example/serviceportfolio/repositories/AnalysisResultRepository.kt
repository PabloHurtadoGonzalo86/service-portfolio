package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.AnalysisResult
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface AnalysisResultRepository : JpaRepository<AnalysisResult, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<AnalysisResult>
    fun findFirstByRepoUrlOrderByCreatedAtDesc(repoUrl: String): Optional<AnalysisResult>
}
