package com.example.serviceportfolio.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User(

    @Column(nullable = false, unique = true)
    var githubId: Long = 0,

    @Column(nullable = false)
    var githubUsername: String = "",

    var email: String? = null,

    var name: String? = null,

    @Column(length = 500)
    var avatarUrl: String? = null,

    @Column(columnDefinition = "text")
    var githubAccessToken: String? = null,

    @Column(columnDefinition = "text")
    var githubRefreshToken: String? = null,

    var githubTokenExpiresAt: Instant? = null,

    @Column(nullable = false, length = 20)
    var plan: String = "FREE",

    @Column(nullable = false)
    var analysesUsed: Int = 0,

    @Column(nullable = false)
    var portfoliosUsed: Int = 0,

    @Column(nullable = false)
    var usageResetAt: Instant = Instant.now(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),

    var lastLoginAt: Instant? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
)
