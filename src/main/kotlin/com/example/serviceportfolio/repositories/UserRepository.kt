package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByGithubId(githubId: Long): Optional<User>
    fun findByGithubUsername(githubUsername: String): Optional<User>
}
