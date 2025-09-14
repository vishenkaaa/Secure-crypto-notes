package com.example.presentation.arch

import androidx.lifecycle.ViewModel
import com.example.presentation.common.utils.ErrorMapper
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

        val mappedError = ErrorMapper.mapError(e)

        lastRetryAction = retryAction
        _baseUiState.update { it.copy(error = mappedError, isLoading = false) }
    }

    fun hasRetryAction(): Boolean {
        return lastRetryAction != null
    }

    fun retryLastAction() {
        clearErrors()
        lastRetryAction?.invoke()
    }

    open fun clearErrors() {
        _baseUiState.update { it.copy(error = null) }
    }
}