package com.example.presentation.feature.home.crypto

import androidx.lifecycle.viewModelScope
import com.example.domain.model.coin.Coin
import com.example.domain.usecase.coin.GetTopCoinsUseCase
import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoVM @Inject constructor(
    private val getTopCoinsUseCase: GetTopCoinsUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CryptoUiState())
    val uiState: StateFlow<CryptoUiState> = _uiState

    init {
        loadTopCoins()
    }

    private fun loadTopCoins() {
        handleLoading(true)
        viewModelScope.launch {
            getTopCoinsUseCase()
                .onSuccess { coins ->
                    _uiState.update { it.copy(coins = coins) }
                }
                .onFailure { error ->
                    handleError(error) { loadTopCoins() }
                }
                .also { handleLoading(false) }
        }
    }
}

data class CryptoUiState(
    val coins: List<Coin> = emptyList()
)