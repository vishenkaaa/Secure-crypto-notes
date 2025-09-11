package com.example.data.local.secure

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit
import androidx.security.crypto.MasterKeys

@Singleton
class SecureStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    companion object {
        const val PREFS_KEY = "secure_prefs"
    }

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs = EncryptedSharedPreferences.create(
        PREFS_KEY,
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun getString(key: String): String? = prefs.getString(key, null)

    fun contains(key: String): Boolean = prefs.contains(key)

    fun clear() = prefs.edit { clear() }
}
