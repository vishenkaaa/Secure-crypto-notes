package com.example.presentation.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.presentation.R
import com.example.presentation.navigation.TopLevelDestinations

@Composable
fun TopLevelDestinations.toTitle(): String{
    return when (this){
        TopLevelDestinations.NOTES -> stringResource(R.string.notes)
        TopLevelDestinations.CRYPTO -> stringResource(R.string.crypto)
    }
}

@Composable
fun TopLevelDestinations.toIcon(): Painter {
    return when (this) {
        TopLevelDestinations.NOTES -> painterResource(R.drawable.ic_notes)
        TopLevelDestinations.CRYPTO -> painterResource(R.drawable.ic_crypto)
    }
}