package com.example.serviceportfolio.repositories

import com.example.serviceportfolio.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByGithubId(githubId: Long): Optional<User>
    fun findByGithubUsername(githubUsername: String): Optional<User>
}
