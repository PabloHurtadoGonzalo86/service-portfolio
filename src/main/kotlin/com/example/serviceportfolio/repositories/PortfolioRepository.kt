package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.Portfolio
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface PortfolioRepository : JpaRepository<Portfolio, Long> {
    fun findAllByOrderByCreatedAtDesc(): List<Portfolio>
    fun findFirstByGithubUsernameOrderByCreatedAtDesc(githubUsername: String): Optional<Portfolio>
}
