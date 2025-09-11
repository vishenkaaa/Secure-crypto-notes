package com.example.data.repositories

import android.util.Base64
import com.example.data.local.secure.SecureStorage
import com.example.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage,
) : AuthRepository {

    companion object {
        const val PIN_HASH = "pin_hash"
        const val PIN_SALT = "pin_salt"
    }

    override suspend fun hasPin(): Boolean =
        secureStorage.contains(PIN_HASH)

    override suspend fun savePin(pin: String) {
        withContext(Dispatchers.IO) {
            val salt = generateSalt()
            val hashedPin = hashPin(pin, salt)

            secureStorage.saveString(PIN_HASH, hashedPin)
            secureStorage.saveString(PIN_SALT, salt)
        }
    }

    override suspend fun verifyPin(pin: String): Boolean {
        val storedHash = secureStorage.getString(PIN_HASH)
        val storedSalt = secureStorage.getString(PIN_SALT) ?: return false

        if (storedHash == null) return false
        val hashedPin = hashPin(pin, storedSalt)
        return hashedPin == storedHash
    }

    private fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)
        return Base64.encodeToString(salt, Base64.DEFAULT)
    }

    private fun hashPin(pin: String, salt: String): String {
        val saltBytes = Base64.decode(salt, Base64.DEFAULT)
        val spec = PBEKeySpec(pin.toCharArray(), saltBytes, 100000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hash, Base64.DEFAULT)
    }
}
