package com.example.presentation.feature.home.crypto.model

import androidx.compose.foundation.lazy.LazyListState
import com.example.domain.model.coin.Coin

data class CryptoUiState(
    val coins: List<Coin> = emptyList(),
    val scrollState: LazyListState = LazyListState()
)