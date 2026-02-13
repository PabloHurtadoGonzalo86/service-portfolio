# Redis Configuration Example

# Add these to your application.yaml or use environment variables

spring:
  # Redis configuration for caching
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 2000ms
      jedis:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
  
  # Cache configuration
  cache:
    type: redis
    redis:
      time-to-live: 3600000  # 1 hour in milliseconds
      cache-null-values: false

# Rate Limiting Configuration (already configured in RateLimitConfig.kt)
# - /api/v1/repos/analyze: 10 requests per minute per IP
# - /api/v1/portfolio/generate: 5 requests per minute per IP
# - Default: 60 requests per minute per IP
