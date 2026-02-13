package com.example.serviceportfolio.services

import com.example.serviceportfolio.entities.User
import com.example.serviceportfolio.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Obtiene o crea un usuario a partir de los datos de OAuth2
     */
    @Transactional
    fun getOrCreateUser(oauth2User: OAuth2User): User {
        val githubId = oauth2User.getAttribute<Int>("id")?.toLong()
            ?: throw IllegalArgumentException("GitHub ID not found in OAuth2User")

        val githubUsername = oauth2User.getAttribute<String>("login")
            ?: throw IllegalArgumentException("GitHub username not found in OAuth2User")

        return userRepository.findByGithubId(githubId).orElseGet {
            val newUser = User(
                githubId = githubId,
                githubUsername = githubUsername,
                name = oauth2User.getAttribute("name"),
                email = oauth2User.getAttribute("email"),
                avatarUrl = oauth2User.getAttribute("avatar_url")
            )
            val saved = userRepository.save(newUser)
            logger.info("Created new user: {} (GitHub ID: {})", githubUsername, githubId)
            saved
        }.also { existingUser ->
            // Update user info if changed
            var updated = false
            if (existingUser.githubUsername != githubUsername) {
                existingUser.githubUsername = githubUsername
                updated = true
            }
            oauth2User.getAttribute<String?>("name")?.let {
                if (existingUser.name != it) {
                    existingUser.name = it
                    updated = true
                }
            }
            oauth2User.getAttribute<String?>("email")?.let {
                if (existingUser.email != it) {
                    existingUser.email = it
                    updated = true
                }
            }
            oauth2User.getAttribute<String?>("avatar_url")?.let {
                if (existingUser.avatarUrl != it) {
                    existingUser.avatarUrl = it
                    updated = true
                }
            }
            if (updated) {
                userRepository.save(existingUser)
                logger.info("Updated user info for: {}", githubUsername)
            }
        }
    }

    fun findByGithubId(githubId: Long): User? {
        return userRepository.findByGithubId(githubId).orElse(null)
    }

    fun findByGithubUsername(githubUsername: String): User? {
        return userRepository.findByGithubUsername(githubUsername).orElse(null)
    }
}
