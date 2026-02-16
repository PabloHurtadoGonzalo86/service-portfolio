package com.example.serviceportfolio.security

import com.example.serviceportfolio.entities.User
import com.example.serviceportfolio.repositories.UserRepository
import com.example.serviceportfolio.util.TokenEncryptor
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
    private val tokenEncryptor: TokenEncryptor
) : DefaultOAuth2UserService() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        val githubId = (oAuth2User.attributes["id"] as Number).toLong()
        val login = oAuth2User.attributes["login"] as String
        val email = oAuth2User.attributes["email"] as? String
        val name = oAuth2User.attributes["name"] as? String
        val avatarUrl = oAuth2User.attributes["avatar_url"] as? String
        val accessToken = userRequest.accessToken.tokenValue

        val user = userRepository.findByGithubId(githubId).orElseGet {
            logger.info("Nuevo usuario registrado: {} (githubId: {})", login, githubId)
            User(githubId = githubId)
        }

        user.githubUsername = login
        user.email = email
        user.name = name
        user.avatarUrl = avatarUrl
        user.githubAccessToken = tokenEncryptor.encrypt(accessToken)
        user.lastLoginAt = Instant.now()

        val savedUser = userRepository.save(user)
        logger.info("Usuario actualizado: {} (id: {})", login, savedUser.id)

        return CustomOAuth2User(oAuth2User, savedUser)
    }
}
