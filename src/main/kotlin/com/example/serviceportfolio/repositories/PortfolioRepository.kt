package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.Portfolio
import com.example.serviceportfolio.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PortfolioRepository : JpaRepository<Portfolio, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<Portfolio>
    fun findFirstByGithubUsernameOrderByCreatedAtDesc(githubUsername: String): Optional<Portfolio>
    fun findAllByUserOrderByCreatedAtDesc(user: User): List<Portfolio>
}
