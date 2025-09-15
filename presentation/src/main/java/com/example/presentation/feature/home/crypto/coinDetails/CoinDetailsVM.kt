package com.example.presentation.feature.home.crypto.coinDetails

import androidx.lifecycle.viewModelScope
import com.example.domain.model.coin.CoinDetails
import com.example.domain.model.coin.MarketChart
import com.example.domain.usecase.coin.GetCoinDetailsUseCase
import com.example.domain.usecase.coin.GetMarketChartUseCase
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
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val getMarketChartUseCase: GetMarketChartUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CoinDetailsUiState())
    val uiState: StateFlow<CoinDetailsUiState> = _uiState.asStateFlow()

    fun loadCoinDetails(id: String) {
        viewModelScope.launch {
            handleLoading(true)
            try {
                val coin = getCoinDetailsUseCase(id)
                _uiState.update {
                    it.copy(coinDetails = coin)
                }

                loadMarketChart(id)
            } catch (e: Exception) {
                handleError(e){ loadCoinDetails(id) }
            } finally {
                handleLoading(false)
            }
        }
    }

    private suspend fun loadMarketChart(id: String) {
        try {
            val marketChart = getMarketChartUseCase(id)
            _uiState.update {
                it.copy(marketChart = marketChart)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

data class CoinDetailsUiState (
    val coinDetails: CoinDetails = CoinDetails(),
    val marketChart: MarketChart? = null
)