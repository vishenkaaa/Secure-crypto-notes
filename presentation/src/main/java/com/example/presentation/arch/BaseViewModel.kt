package com.example.presentation.arch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

open class BaseViewModel : ViewModel() {

    private val _baseUiState = MutableStateFlow(BaseUiState())
    val baseUiState: StateFlow<BaseUiState> = _baseUiState.asStateFlow()

    private var lastRetryAction: (() -> Unit)? = null

    protected open fun handleLoading(isLoading: Boolean) {
        _baseUiState.update { it.copy(isLoading = isLoading) }
    }

    protected open fun handleError(
        e: Throwable,
        retryAction: (() -> Unit)? = null
    ) {
        e.printStackTrace()

        lastRetryAction = retryAction

        _baseUiState.update {
            when (e) {
                is java.net.UnknownHostException,
                is java.net.SocketTimeoutException,
                is java.io.IOException -> {
                    it.copy(isConnectionError = true, isLoading = false)
                }
                else -> {
                    it.copy(unexpectedError = e.localizedMessage, isLoading = false)
                }
            }
        }
    }

    fun hasRetryAction(): Boolean {
        return lastRetryAction != null
    }

    fun retryLastAction() {
        clearErrors()
        lastRetryAction?.invoke()
    }

    open fun clearErrors() {
        _baseUiState.update {
            it.copy(
                unexpectedError = null,
                isConnectionError = false
            )
        }
        lastRetryAction = null
    }
}