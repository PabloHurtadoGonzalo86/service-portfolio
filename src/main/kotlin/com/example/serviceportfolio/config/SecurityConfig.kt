package com.example.serviceportfolio.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun authorizedClientService(
        jdbcTemplate: JdbcTemplate,
        clientRegistrationRepository: ClientRegistrationRepository
    ): OAuth2AuthorizedClientService {
        return JdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/v1/repos/analyze").permitAll()
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { }
            .csrf { it.disable() }
        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")  // TODO: restringir en producción
        configuration.allowedMethods = listOf("GET", "POST", "OPTIONS")
        configuration.allowedHeaders = listOf("Content-Type", "Authorization")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/api/**", configuration)
        return source
    }

}
