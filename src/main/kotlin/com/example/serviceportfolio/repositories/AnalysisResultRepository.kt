package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.AnalysisResult
import org.springframework.data.jpa.repository.JpaRepository

interface AnalysisResultRepository : JpaRepository<AnalysisResult, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<AnalysisResult>
}
