package com.example.serviceportfolio.client

import com.example.serviceportfolio.config.GitHubAppProperties
import org.kohsuke.github.GitHubBuilder
import org.kohsuke.github.extras.authorization.JWTTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.time.Instant

@Service
class GitHubAppTokenService(
    private val properties: GitHubAppProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private var cachedToken: String? = null
    private var tokenExpiresAt: Instant? = null

    fun getInstallationToken(): String {
        // Si el token cacheado sigue valido (expira en mas de 5 min), devolverlo
        val expires = tokenExpiresAt
        if (cachedToken != null && expires != null && Instant.now().isBefore(expires.minusSeconds(300))) {
            return cachedToken!!
        }

        logger.info("Generating new GitHub App installation token")
        return refreshToken()
    }

    private fun refreshToken(): String {
        // Crear JWTTokenProvider: genera el JWT automaticamente a partir del appId + clave privada
        val jwtProvider = JWTTokenProvider(properties.appId.toString(), Path.of(properties.privateKeyPath))

        // Conectar como GitHub App usando el JWTTokenProvider
        val appGitHub = GitHubBuilder()
            .withAuthorizationProvider(jwtProvider)
            .build()

        // Obtener la instalacion y crear token
        val installation = appGitHub.app.getInstallationById(properties.installationId)
        val token = installation.createToken().create()

        // Cachear - getExpiresAt() ya devuelve Instant directamente
        cachedToken = token.token
        tokenExpiresAt = token.expiresAt

        logger.info("GitHub App installation token generated, expires at: {}", tokenExpiresAt)
        return cachedToken!!
    }
}