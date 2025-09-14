package com.example.domain.usecase.auth

import com.example.domain.repository.AuthRepository
import javax.inject.Inject

class HasPinUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = repository.hasPin()
}