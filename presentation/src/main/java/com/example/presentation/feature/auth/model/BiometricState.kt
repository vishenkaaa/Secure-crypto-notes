package com.example.presentation.feature.auth.model

sealed class BiometricState {
    data object Idle : BiometricState()
    data object Authenticating : BiometricState()
    data object Success : BiometricState()
    data object Failed : BiometricState()
    data class Error(val message: String) : BiometricState()
    data object UserCanceled : BiometricState()
}