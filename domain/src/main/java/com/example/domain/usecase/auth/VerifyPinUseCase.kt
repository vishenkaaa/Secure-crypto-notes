package com.example.domain.usecase.auth

import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class VerifyPinUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(pin: String): Boolean =
        repository.verifyPin(pin)
}
