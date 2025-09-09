package com.example.presentation

import androidx.lifecycle.viewModelScope
import com.example.presentation.arch.BaseViewModel
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
) : BaseViewModel() {
    private val _shouldShowBottomBar = MutableStateFlow(false)
    val shouldShowBottomBar: StateFlow<Boolean> = _shouldShowBottomBar.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private var lastBackPressTime: Long = 0
    private var currentRoute: String? = null

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

        return route != Home.CryptoDetails::class.qualifiedName
    }
}

sealed class UiEvent {
    data object ExitApp : UiEvent()
    data object ShowExitMessage : UiEvent()
    data object NavigateBack : UiEvent()
}