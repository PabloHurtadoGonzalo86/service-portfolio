package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.Portfolio
import org.springframework.data.jpa.repository.JpaRepository

interface PortfolioRepository : JpaRepository<Portfolio, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<Portfolio>
}
