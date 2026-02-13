package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.AsyncJob
import org.springframework.data.jpa.repository.JpaRepository

interface AsyncJobRepository : JpaRepository<AsyncJob, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<AsyncJob>
}
