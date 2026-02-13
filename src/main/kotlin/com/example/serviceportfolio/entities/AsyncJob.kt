package com.example.serviceportfolio.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

enum class AsyncJobStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}

@Entity
@Table(name = "async_jobs")
class AsyncJob(

    @Column(nullable = false)
    var jobType: String = "",

    @Column(nullable = false)
    var githubUsername: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AsyncJobStatus = AsyncJobStatus.PENDING,

    @Column(columnDefinition = "text")
    var errorMessage: String? = null,

    @Column
    var resultId: Long? = null,

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
