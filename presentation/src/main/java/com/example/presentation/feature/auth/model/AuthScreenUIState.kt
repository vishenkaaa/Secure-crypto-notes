package com.example.presentation.feature.auth.model

data class AuthScreenUIState(
    val currentPin: String = "",
    val confirmPin: String = "",
    val step: AuthStep = AuthStep.INITIAL,
    val isComplete: Boolean = false,
    val canUseBiometric: Boolean = false,
    val showBiometric: Boolean = false,
    val isCreatingPin: Boolean = false,
    val isLoading: Boolean = false
)