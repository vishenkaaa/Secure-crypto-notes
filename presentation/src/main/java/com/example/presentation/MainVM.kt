package com.example.presentation

import androidx.lifecycle.viewModelScope
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.common.utils.AuthState
import com.example.presentation.common.utils.AuthStateManager
import com.example.presentation.navigation.Graphs
import com.example.presentation.navigation.Home
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val authStateManager: AuthStateManager,
) : BaseViewModel() {

    val authState: StateFlow<AuthState> = authStateManager.authState

    private val _shouldShowBottomBar = MutableStateFlow(false)
    val shouldShowBottomBar: StateFlow<Boolean> = _shouldShowBottomBar.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private var lastBackPressTime: Long = 0
    private var currentRoute: String? = null

    private var backgroundStartTime: Long = 0

    fun onAppBackgrounded() {
        backgroundStartTime = System.currentTimeMillis()
    }

    fun onAppResumed() {
        val timeInBackground = System.currentTimeMillis() - backgroundStartTime

        if (timeInBackground > 5_000 && backgroundStartTime!=0L){
            authStateManager.setAuthState(AuthState.NeedsAuth)
        }
    }

    fun lockApp() {
        authStateManager.setAuthState(AuthState.NeedsAuth)
        backgroundStartTime = 0L
    }

    fun onBackPressed() {
        viewModelScope.launch {
            if (isDestinationRoot(currentRoute)) {
                val currentTime = System.currentTimeMillis()
                val timeDiff = currentTime - lastBackPressTime

                if (timeDiff < 2000) {
                    _uiEvents.emit(UiEvent.ExitApp)
                } else {
                    lastBackPressTime = currentTime
                    _uiEvents.emit(UiEvent.ShowExitMessage)
                }
            } else {
                _uiEvents.emit(UiEvent.NavigateBack)
            }
        }
    }

    fun onDestinationChanged(route: String?) {
        currentRoute = route
        _shouldShowBottomBar.value = isMainTabRoute(route)
    }

    private fun isMainTabRoute(route: String?): Boolean {
        if (route == null) return false

        return route == Home.Crypto::class.qualifiedName || route == Home.Notes::class.qualifiedName
    }

    private fun isDestinationRoot(route: String?): Boolean {
        if (route == null) return false

        return route == Home.Crypto::class.qualifiedName ||
                route == Home.Notes::class.qualifiedName ||
                route == Graphs.Auth::class.qualifiedName
    }
}

sealed class UiEvent {
    data object ExitApp : UiEvent()
    data object ShowExitMessage : UiEvent()
    data object NavigateBack : UiEvent()
}