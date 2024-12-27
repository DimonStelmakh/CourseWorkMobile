package com.example.courseworkmobile.data.local

import at.favre.lib.crypto.bcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordManager @Inject constructor() {
    private val bcrypt = BCrypt.withDefaults()

    fun hashPassword(password: String): String {
        return bcrypt.hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.verifyer().verify(
            password.toCharArray(),
            hashedPassword.toCharArray()
        ).verified
    }
}