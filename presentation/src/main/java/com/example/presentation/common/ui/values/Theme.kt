package com.example.presentation.common.ui.values

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme
    @Composable
    get() = lightColorScheme(
        primary = Blue,
        onPrimary = White,
        background = Background,
        onBackground = White,
        primaryContainer = Dark,
        onPrimaryContainer = White,
        error = Red,
        onError = White,
        surfaceVariant = Shadow
    )

@Composable
fun SecureCryptoNotesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}