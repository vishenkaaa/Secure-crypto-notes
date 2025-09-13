package com.example.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.domain.model.exception.BaseException
import com.example.presentation.R
import com.example.presentation.arch.ErrorConfig

@Composable
fun BaseException.toErrorConfig(
    onRetry: (() -> Unit)? = null
): ErrorConfig {
    return when (this) {
        is BaseException.ConnectionErrorException,
        is BaseException.TimeoutException ->
            ErrorConfig(
                message = stringResource(R.string.connection_error),
                showRetry = onRetry != null
            )

        is BaseException.UnauthorizedException ->
            ErrorConfig(message = stringResource(R.string.unauthorized))

        is BaseException.ForbiddenException ->
            ErrorConfig(message = stringResource(R.string.forbidden))

        is BaseException.TooManyRequestsException ->
            ErrorConfig(message = stringResource(R.string.too_many_requests))

        is BaseException.ServerErrorException,
        is BaseException.ServiceUnavailableException ->
            ErrorConfig(
                message = stringResource(R.string.server_error),
                showRetry = onRetry != null
            )

        is BaseException.ApiErrorException ->
            ErrorConfig(message = error ?: stringResource(R.string.unexpected_error))

        else ->
            ErrorConfig(
                message = error ?: stringResource(R.string.unexpected_error),
                showRetry = onRetry != null
            )
    }
}