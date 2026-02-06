package com.example.serviceportfolio.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/v1/repos/analyze").permitAll()
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/").permitAll()
                    .anyRequest().authenticated()
            }
            //.oauth2Login { }  // Configura login con OAuth2 (GitHub)
            .csrf { it.disable() }  // Para API REST stateless
        return http.build()
    }

}