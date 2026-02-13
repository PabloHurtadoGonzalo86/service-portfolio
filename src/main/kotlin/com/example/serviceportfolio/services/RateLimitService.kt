package com.example.serviceportfolio.services

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Service
class RateLimitService {

    private val buckets = ConcurrentHashMap<String, Bucket>()

    /**
     * Verifica si una solicitud está permitida bajo los límites de tasa
     * @param key Identificador único (ej: "user:123:portfolio" o "ip:192.168.1.1:analyze")
     * @param capacity Número máximo de tokens
     * @param refillTokens Tokens a recargar
     * @param refillDuration Periodo de recarga
     * @return true si la solicitud está permitida, false si excede el límite
     */
    fun tryConsume(
        key: String,
        capacity: Long = 10,
        refillTokens: Long = 10,
        refillDuration: Duration = Duration.ofHours(1)
    ): Boolean {
        val bucket = buckets.computeIfAbsent(key) {
            createBucket(capacity, refillTokens, refillDuration)
        }
        return bucket.tryConsume(1)
    }

    /**
     * Obtiene el número de tokens disponibles
     */
    fun getAvailableTokens(key: String): Long {
        return buckets[key]?.availableTokens ?: 0
    }

    private fun createBucket(
        capacity: Long,
        refillTokens: Long,
        refillDuration: Duration
    ): Bucket {
        val limit = Bandwidth.classic(
            capacity,
            Refill.intervally(refillTokens, refillDuration)
        )
        return Bucket.builder()
            .addLimit(limit)
            .build()
    }
}
