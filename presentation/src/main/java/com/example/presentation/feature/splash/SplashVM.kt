package com.example.presentation.feature.splash

import com.example.presentation.arch.BaseViewModel
import com.example.presentation.manager.AuthState
import com.example.presentation.manager.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashVM @Inject constructor(
    private val authStateManager: AuthStateManager
) : BaseViewModel() {
    fun setNeedAuthState(){
        authStateManager.setAuthState(AuthState.NeedsAuth)
    }
}