package com.example.serviceportfolio.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "users")
class User(

    @Column(nullable = false, unique = true)
    var githubId: Long = 0,

    @Column(nullable = false, unique = true)
    var githubUsername: String = "",

    @Column
    var name: String? = null,

    @Column
    var email: String? = null,

    @Column
    var avatarUrl: String? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
)
