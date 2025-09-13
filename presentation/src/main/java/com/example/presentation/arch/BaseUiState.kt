package com.example.presentation.arch

import com.example.domain.model.exception.BaseException

data class BaseUiState(
    val isLoading: Boolean = false,
    val error: BaseException? = null
) {
    val hasError: Boolean get() = error != null
}