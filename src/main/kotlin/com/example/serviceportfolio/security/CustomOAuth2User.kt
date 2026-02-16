package com.example.serviceportfolio.security

import com.example.serviceportfolio.entities.User
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOAuth2User(
    private val delegate: OAuth2User,
    val user: User
) : OAuth2User by delegate
