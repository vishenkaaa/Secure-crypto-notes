package com.example.data.local.secure

import android.util.Base64
import net.sqlcipher.database.SQLiteDatabase
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabasePasswordManager @Inject constructor(
    private val secureStorage: SecureStorage
) {
    companion object {
        private const val DB_PASSWORD_KEY = "db_password"
    }

    fun getDatabasePassword(): ByteArray {
        var password = secureStorage.getString(DB_PASSWORD_KEY)

        if (password == null) {
            password = generateSecurePassword()
            secureStorage.saveString(DB_PASSWORD_KEY, password)
        }

        return SQLiteDatabase.getBytes(password.toCharArray())
    }

    private fun generateSecurePassword(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}