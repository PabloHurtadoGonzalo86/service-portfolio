package com.example.serviceportfolio.config

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertNotNull

@SpringBootTest
@TestPropertySource(properties = [
    "spring.cache.type=simple"  // Use simple cache for testing instead of Redis
])
class CacheConfigTest {

    @Autowired(required = false)
    private lateinit var cacheManager: CacheManager

    @Test
    fun `cache manager should be configured`() {
        assertNotNull(cacheManager, "CacheManager should be available")
    }

    @Test
    fun `should have cache names available`() {
        val cacheNames = cacheManager.cacheNames
        assertNotNull(cacheNames, "Cache names should be available")
    }
}
