package com.example.presentation.feature.home.crypto

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.viewModelScope
import com.example.domain.model.coin.Coin
import com.example.domain.usecase.coin.AddFavoriteCoinUseCase
import com.example.domain.usecase.coin.GetFavoriteCoinsUseCase
import com.example.domain.usecase.coin.GetTopCoinsUseCase
import com.example.domain.usecase.coin.RemoveFavoriteCoinUseCase
import com.example.presentation.arch.BaseViewModel
import com.example.presentation.feature.home.crypto.model.CryptoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoVM @Inject constructor(
    private val getTopCoinsUseCase: GetTopCoinsUseCase,
    private val getFavoriteCoinsUseCase: GetFavoriteCoinsUseCase,
    private val addFavoriteCoinUseCase: AddFavoriteCoinUseCase,
    private val removeFavoriteCoinUseCase: RemoveFavoriteCoinUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CryptoUiState())
    val uiState: StateFlow<CryptoUiState> = _uiState

    init {
        loadCoins()
    }

    private fun loadCoins() {
        handleLoading(true)
        viewModelScope.launch {
            getTopCoinsUseCase()
                .onSuccess { topCoins ->
                    combine(
                        getFavoriteCoinsUseCase(), MutableStateFlow(topCoins)
                    ) { favorites, coins ->
                        coins.map { coin ->
                            coin.copy(isFavorite = favorites.any { it.id == coin.id })
                        }.sortedByDescending { it.isFavorite }
                    }.onEach { mergedCoins ->
                        _uiState.update { it.copy(coins = mergedCoins) }
                        handleLoading(false)
                    }.catch { e ->
                        handleError(e) { loadCoins() }
                    }.launchIn(viewModelScope)

                }.onFailure { error ->
                    handleError(error) { loadCoins() }
                    handleLoading(false)
                }
        }
    }

    fun toggleFavorite(coin: Coin) {
        viewModelScope.launch {
            if (coin.isFavorite) {
                removeFavoriteCoinUseCase(coin)
            } else {
                addFavoriteCoinUseCase(coin)
            }

            _uiState.update { state ->
                val updatedCoins = state.coins.map {
                    if (it.id == coin.id) it.copy(isFavorite = !it.isFavorite) else it
                }.sortedByDescending { it.isFavorite }
                state.copy(coins = updatedCoins)
            }
        }
    }

    fun updateScrollState(newState: LazyListState) {
        _uiState.update { it.copy(scrollState = newState) }
    }
}