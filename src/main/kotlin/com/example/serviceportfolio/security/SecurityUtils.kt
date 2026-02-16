package com.example.serviceportfolio.security

import com.example.serviceportfolio.entities.User
import com.example.serviceportfolio.exceptions.AuthenticationRequiredException
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {

    fun getCurrentUser(): User? {
        val authentication = SecurityContextHolder.getContext().authentication ?: return null
        val principal = authentication.principal
        return (principal as? CustomOAuth2User)?.user
    }

    fun requireCurrentUser(): User {
        return getCurrentUser()
            ?: throw AuthenticationRequiredException("User not authenticated")
    }
}
