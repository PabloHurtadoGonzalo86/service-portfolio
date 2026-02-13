package com.example.serviceportfolio.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig(
    @Value("\${app.async.core-pool-size:2}")
    private val corePoolSize: Int,
    
    @Value("\${app.async.max-pool-size:5}")
    private val maxPoolSize: Int,
    
    @Value("\${app.async.queue-capacity:50}")
    private val queueCapacity: Int
) : AsyncConfigurer {

    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = corePoolSize
        executor.maxPoolSize = maxPoolSize
        executor.queueCapacity = queueCapacity
        executor.setThreadNamePrefix("async-portfolio-")
        executor.initialize()
        return executor
    }
}
