package com.example.serviceportfolio.controller

import com.example.serviceportfolio.dtos.UserResponse
import com.example.serviceportfolio.security.SecurityUtils
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication and session management")
class AuthController {

    @Operation(summary = "Get current user", description = "Returns the authenticated user's profile information")
    @ApiResponse(responseCode = "200", description = "User profile returned")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<UserResponse> {
        val user = SecurityUtils.requireCurrentUser()
        return ResponseEntity.ok(
            UserResponse(
                id = user.id!!,
                githubUsername = user.githubUsername,
                name = user.name,
                email = user.email,
                avatarUrl = user.avatarUrl,
                plan = user.plan,
                analysesUsed = user.analysesUsed,
                portfoliosUsed = user.portfoliosUsed
            )
        )
    }

    @Operation(summary = "Logout", description = "Invalidates the current session and clears security context")
    @ApiResponse(responseCode = "200", description = "Logged out successfully")
    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Void> {
        request.session.invalidate()
        SecurityContextHolder.clearContext()
        return ResponseEntity.ok().build()
    }
}
