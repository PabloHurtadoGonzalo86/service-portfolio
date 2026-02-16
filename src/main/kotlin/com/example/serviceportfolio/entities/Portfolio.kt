package com.example.serviceportfolio.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "portfolios")
class Portfolio(

    @Column(nullable = false)
    var githubUsername: String = "",

    @Column(nullable = false, columnDefinition = "text")
    var portfolioData: String = "{}",

    @Column(nullable = false)
    var totalPublicRepos: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
)
