package com.example.presentation.feature.home.crypto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.presentation.R
import com.example.presentation.common.ui.components.CenterAlignedHeader
import com.example.presentation.common.ui.components.HandleError
import com.example.presentation.common.ui.components.LoadingBackground

@Composable
fun CryptoScreen(
    viewModel: CryptoVM = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = { CenterAlignedHeader(stringResource(R.string.app_name)) },
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

            }
        }
    }

    HandleError(
        baseUiState = baseUiState,
        onErrorConsume = { viewModel.clearErrors() },
        onRetry = { if (viewModel.hasRetryAction()) viewModel.retryLastAction() else null }
    )

    LoadingBackground(baseUiState.isLoading)
}