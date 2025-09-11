package com.example.presentation.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManager @Inject constructor() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun setAuthState(newAuthState: AuthState) {
        _authState.value = newAuthState
    }
}

sealed class AuthState {
    data object Loading : AuthState()
    data object Authenticated : AuthState()
    data object NeedsAuth : AuthState()
}