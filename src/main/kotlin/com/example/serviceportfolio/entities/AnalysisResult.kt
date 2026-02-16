package com.example.serviceportfolio.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

@Entity
@Table(name = "analysis_results")
class AnalysisResult(

    @Column(nullable = false)
    var repoUrl: String = "",

    @Column(nullable = false)
    var projectName: String = "",

    @Column(nullable = false, columnDefinition = "text")
    var shortDescription: String = "",

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    var techStack: List<String> = emptyList(),

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    var detectedFeatures: List<String> = emptyList(),

    @Column(nullable = false, columnDefinition = "text")
    var readmeContent: String = "",

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
