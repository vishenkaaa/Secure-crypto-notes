package com.example.presentation.feature.home.crypto.coinDetails

import androidx.lifecycle.viewModelScope
import com.example.domain.model.coin.CoinDetails
import com.example.domain.repository.CoinRepository
import com.example.presentation.arch.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinDetailsVM @Inject constructor(
    private val repository: CoinRepository,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CoinDetailsUiState())
    val uiState: StateFlow<CoinDetailsUiState> = _uiState.asStateFlow()

    fun loadCoinDetails(id: String) {
        viewModelScope.launch {
            handleLoading(true)
            try {
                val coin = repository.getCoinDetails(id)
                _uiState.update {
                    it.copy(coinDetails = coin)
                }
            } catch (e: Exception) {
                handleError(e){ loadCoinDetails(id) }
            } finally {
                handleLoading(false)
            }
        }
    }
}

data class CoinDetailsUiState (
    val coinDetails: CoinDetails = CoinDetails()
)