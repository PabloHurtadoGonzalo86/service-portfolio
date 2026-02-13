package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.Portfolio
import com.example.serviceportfolio.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface PortfolioRepository : JpaRepository<Portfolio, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<Portfolio>
    fun findAllByUserOrderByCreatedAtDesc(user: User): List<Portfolio>
    fun findByIdAndUser(id: Long, user: User): Portfolio?
}
