package com.example.serviceportfolio.util

import org.springframework.security.crypto.encrypt.TextEncryptor
import org.springframework.stereotype.Component

@Component
class TokenEncryptor(
    private val textEncryptor: TextEncryptor
) {

    fun encrypt(plainText: String?): String? {
        return plainText?.let { textEncryptor.encrypt(it) }
    }

    fun decrypt(encryptedText: String?): String? {
        return encryptedText?.let { textEncryptor.decrypt(it) }
    }
}
