package com.example.serviceportfolio.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.encrypt.Encryptors
import org.springframework.security.crypto.encrypt.TextEncryptor

@Configuration
class EncryptionConfig(
    @Value("\${app.encryption.password}")
    private val password: String,

    @Value("\${app.encryption.salt}")
    private val salt: String
) {

    @Bean
    fun textEncryptor(): TextEncryptor {
        return Encryptors.text(password, salt)
    }
}
