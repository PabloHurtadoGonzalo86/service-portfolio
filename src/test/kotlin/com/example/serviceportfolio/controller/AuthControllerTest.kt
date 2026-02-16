package com.example.serviceportfolio.controller

import com.example.serviceportfolio.entities.User
import com.example.serviceportfolio.security.CustomOAuth2User
import com.example.serviceportfolio.security.CustomOAuth2UserService
import com.example.serviceportfolio.security.OAuth2LoginSuccessHandler
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(AuthController::class)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var customOAuth2UserService: CustomOAuth2UserService

    @MockitoBean
    private lateinit var oAuth2LoginSuccessHandler: OAuth2LoginSuccessHandler

    private fun createTestUser(): User {
        return User(
            githubId = 12345L,
            githubUsername = "testuser",
            email = "test@example.com",
            name = "Test User",
            avatarUrl = "https://avatars.githubusercontent.com/u/12345"
        ).apply { id = UUID.fromString("00000000-0000-0000-0000-000000000001") }
    }

    private fun createCustomOAuth2User(user: User): CustomOAuth2User {
        val attributes = mapOf<String, Any>(
            "id" to user.githubId,
            "login" to user.githubUsername,
            "name" to (user.name ?: ""),
            "email" to (user.email ?: ""),
            "avatar_url" to (user.avatarUrl ?: "")
        )
        val authority = OAuth2UserAuthority(attributes)
        val defaultOAuth2User = DefaultOAuth2User(listOf(authority), attributes, "login")
        return CustomOAuth2User(defaultOAuth2User, user)
    }

    @Test
    fun `get me returns 200 with user data`() {
        val user = createTestUser()
        val customUser = createCustomOAuth2User(user)

        mockMvc.perform(
            get("/api/v1/auth/me")
                .with(oauth2Login().oauth2User(customUser))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("00000000-0000-0000-0000-000000000001"))
            .andExpect(jsonPath("$.githubUsername").value("testuser"))
            .andExpect(jsonPath("$.name").value("Test User"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.avatarUrl").value("https://avatars.githubusercontent.com/u/12345"))
            .andExpect(jsonPath("$.plan").value("FREE"))
            .andExpect(jsonPath("$.analysesUsed").value(0))
            .andExpect(jsonPath("$.portfoliosUsed").value(0))
    }

    @Test
    fun `get me without auth redirects to login`() {
        mockMvc.perform(get("/api/v1/auth/me"))
            .andExpect(status().is3xxRedirection)
    }

    @Test
    fun `get me with default oauth2Login returns 401 when no custom principal`() {
        mockMvc.perform(
            get("/api/v1/auth/me")
                .with(oauth2Login())
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `logout returns 200`() {
        val user = createTestUser()
        val customUser = createCustomOAuth2User(user)

        mockMvc.perform(
            post("/api/v1/auth/logout")
                .with(oauth2Login().oauth2User(customUser))
                .with(csrf())
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `logout without auth redirects to login`() {
        mockMvc.perform(
            post("/api/v1/auth/logout")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
    }
}
