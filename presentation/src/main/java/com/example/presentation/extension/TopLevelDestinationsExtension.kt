package com.example.presentation.extension

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
        TopLevelDestinations.NOTES -> rememberVectorPainter(Icons.Filled.Description)
        TopLevelDestinations.CRYPTO -> rememberVectorPainter(Icons.Filled.ShowChart)
    }
}