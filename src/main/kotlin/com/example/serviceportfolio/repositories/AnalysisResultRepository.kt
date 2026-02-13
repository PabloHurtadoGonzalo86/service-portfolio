package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.AnalysisResult
import com.example.serviceportfolio.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface AnalysisResultRepository : JpaRepository<AnalysisResult, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<AnalysisResult>
    fun findAllByUserOrderByCreatedAtDesc(user: User): List<AnalysisResult>
    fun findByIdAndUser(id: Long, user: User): AnalysisResult?
}
