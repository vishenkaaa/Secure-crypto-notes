package com.example.presentation.common.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState
import com.example.presentation.extension.toErrorConfig

@Composable
fun HandleError(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    baseUiState: BaseUiState,
    onErrorConsume: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null
) {
    val error = baseUiState.error ?: return
    val config = error.toErrorConfig(onRetry)

    ErrorSnackBar(
        snackBarHostState = snackbarHostState,
        error = config.message,
        actionLabel = if (onRetry != null) stringResource(R.string.retry) else null,
        duration = SnackbarDuration.Indefinite,
        onErrorDismissed = onErrorConsume,
        onErrorConsumed = onRetry
    )
}