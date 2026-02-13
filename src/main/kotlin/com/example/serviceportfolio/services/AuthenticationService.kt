package com.example.serviceportfolio.services

import com.example.serviceportfolio.entities.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val userService: UserService
) {

    /**
     * Obtiene el usuario autenticado actual desde el contexto de seguridad
     * @return User si está autenticado, null si es una solicitud anónima
     */
    fun getCurrentUser(): User? {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: return null

        if (authentication !is OAuth2AuthenticationToken) {
            return null
        }

        val oauth2User = authentication.principal as? OAuth2User
            ?: return null

        return userService.getOrCreateUser(oauth2User)
    }

    /**
     * Obtiene el usuario autenticado actual, lanza excepción si no está autenticado
     */
    fun requireCurrentUser(): User {
        return getCurrentUser()
            ?: throw IllegalStateException("User must be authenticated")
    }
}
