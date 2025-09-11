package com.example.presentation.common.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.example.presentation.R
import com.example.presentation.arch.BaseUiState

@Composable
fun HandleError(
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    baseUiState: BaseUiState,
    onErrorConsume: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null
) {
    when {
        baseUiState.isConnectionError -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(5f),
                contentAlignment = Alignment.BottomCenter,
            ) {
                ConnectionErrorSnackBar(
                    snackBarHostState = snackbarHostState,
                    onErrorDismissed = { onErrorConsume?.invoke() },
                    onErrorConsumed = onRetry
                )
            }
        }

        baseUiState.unexpectedError?.isNotBlank() == true -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(5f),
                contentAlignment = Alignment.BottomCenter
            ) {
                ErrorSnackBar(
                    snackBarHostState = snackbarHostState,
                    error = baseUiState.unexpectedError.toString(),
                    actionLabel = if (onRetry != null) stringResource(R.string.retry) else null,
                    onErrorConsumed = onRetry,
                    onErrorDismissed = onErrorConsume
                )
            }
        }
    }
}