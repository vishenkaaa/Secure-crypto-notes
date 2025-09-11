package com.example.domain.repository

interface AuthRepository {
    suspend fun hasPin(): Boolean
    suspend fun savePin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
}